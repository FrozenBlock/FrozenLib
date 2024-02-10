/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.api;

import io.netty.buffer.Unpooled;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindManager {
	private static final long MIN_TIME_VALUE = Long.MIN_VALUE + 1;
	public static final Map<Function<WindManager, WindManagerExtension>, Integer> EXTENSION_PROVIDERS = new Object2ObjectOpenHashMap<>();

	public final List<WindManagerExtension> attachedExtensions;

	public boolean overrideWind;
	public long time;
	public Vec3 commandWind = Vec3.ZERO;
	public double windX;
	public double windY;
	public double windZ;
	public double laggedWindX;
	public double laggedWindY;
	public double laggedWindZ;
	public long seed = 0;

	private final ServerLevel level;

	@SuppressWarnings("unchecked")
	public WindManager(@NotNull ServerLevel level) {
		this.level = level;
		this.setSeed(level.getRandom().nextLong());
		List<WindManagerExtension> extensions = new ObjectArrayList<>();
		Map.Entry<Function<WindManager, WindManagerExtension>, Integer>[] extensionProviders = EXTENSION_PROVIDERS.entrySet().toArray(new Map.Entry[0]);
		Arrays.sort(extensionProviders, Map.Entry.comparingByValue());

		for (Map.Entry<Function<WindManager, WindManagerExtension>, Integer> extensionFunc : extensionProviders) {
			var extension = extensionFunc.getKey().apply(this);
			extensions.add(extension);
		}
		this.attachedExtensions = extensions;
	}

	public static void addExtension(Function<WindManager, WindManagerExtension> extension, int priority) {
		if (extension != null) EXTENSION_PROVIDERS.put(extension, priority);
	}

	public static void addExtension(Function<WindManager, WindManagerExtension> extension) {
		addExtension(extension, 1000);
	}

	public ImprovedNoise perlinChecked = new ImprovedNoise(new LegacyRandomSource(this.seed));
	public ImprovedNoise perlinLocal = new ImprovedNoise(new SingleThreadedRandomSource(this.seed));
	public ImprovedNoise perlinXoro = new ImprovedNoise(new XoroshiroRandomSource(this.seed));

	public void setSeed(long newSeed) {
		if (newSeed != this.seed) {
			this.seed = newSeed;
			this.perlinChecked = new ImprovedNoise(new LegacyRandomSource(this.seed));
			this.perlinLocal = new ImprovedNoise(new SingleThreadedRandomSource(this.seed));
			this.perlinXoro = new ImprovedNoise(new XoroshiroRandomSource(this.seed));
		}
	}

	@NotNull
	public static WindManager getWindManager(@NotNull ServerLevel level) {
		return ((WindManagerInterface)level).frozenLib$getWindManager();
	}

	@NotNull
	public SavedData.Factory<WindStorage> createData() {
		return new SavedData.Factory<>(
			() -> new WindStorage(this),
			tag -> WindStorage.load(tag, this),
			DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
		);
	}

	public void tick(@NotNull ServerLevel level) {
		this.runResetsIfNeeded();

		this.time += 1;
		//WIND
		float thunderLevel = this.level.getThunderLevel(1F) * 0.03F;
		double calcTime = this.time * 0.0005;
		double calcTimeY = this.time * 0.00035;
		Vec3 vec3 = sampleVec3(this.perlinXoro, calcTime, calcTimeY, calcTime);
		this.windX = vec3.x + (vec3.x * thunderLevel);
		this.windY = vec3.y + (vec3.y * thunderLevel);
		this.windZ = vec3.z + (vec3.z * thunderLevel);
		//LAGGED WIND
		double calcLaggedTime = (this.time - 40) * 0.0005;
		double calcLaggedTimeY = (this.time - 60) * 0.00035;
		Vec3 laggedVec = sampleVec3(this.perlinXoro, calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		this.laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		this.laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		this.laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);

		//EXTENSIONS
		for (WindManagerExtension extension : this.attachedExtensions) {
			extension.baseTick(level);
			extension.tick(level);
		}

		//SYNC WITH CLIENTS IN CASE OF DESYNC
		if (this.time % 20 == 0) {
			this.sendSync(this.level);
		}
	}

	//Reset values in case of potential overflow
	private boolean runResetsIfNeeded() {
		boolean needsReset = false;
		if (Math.abs(this.time) == Long.MAX_VALUE) {
			needsReset = true;
			this.time = MIN_TIME_VALUE;
		}
		if (Math.abs(this.windX) == Double.MAX_VALUE) {
			needsReset = true;
			this.windX = 0;
		}
		if (Math.abs(this.windY) == Double.MAX_VALUE) {
			needsReset = true;
			this.windY = 0;
		}
		if (Math.abs(this.windZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.windZ = 0;
		}
		if (Math.abs(this.laggedWindX) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindX = 0;
		}
		if (Math.abs(this.laggedWindY) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindY = 0;
		}
		if (Math.abs(this.laggedWindZ) == Double.MAX_VALUE) {
			needsReset = true;
			this.laggedWindZ = 0;
		}

		//EXTENSIONS
		for (WindManagerExtension extension : this.attachedExtensions) {
			if (extension.runResetsIfNeeded()) {
				needsReset = true;
			}
		}

		if (needsReset) {
			this.sendSync(this.level);
		}
		return needsReset;
	}

	@NotNull
	public FriendlyByteBuf createSyncByteBuf() {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(this.time);
		byteBuf.writeLong(this.seed);
		byteBuf.writeBoolean(this.overrideWind);
		byteBuf.writeDouble(this.commandWind.x());
		byteBuf.writeDouble(this.commandWind.y());
		byteBuf.writeDouble(this.commandWind.z());

		//EXTENSIONS
		for (WindManagerExtension extension : this.attachedExtensions) {
			extension.createSyncByteBuf(byteBuf);
		}

		return byteBuf;
	}

	public void sendSync(@NotNull ServerLevel level) {
		FriendlyByteBuf byteBuf = this.createSyncByteBuf();
		for (ServerPlayer player : PlayerLookup.world(level)) {
			this.sendSyncToPlayer(byteBuf, player);
		}
	}

	public void sendSyncToPlayer(@NotNull FriendlyByteBuf byteBuf, @NotNull ServerPlayer player) {
		ServerPlayNetworking.send(player, FrozenNetworking.WIND_SYNC_PACKET, byteBuf);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos));
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos, double multiplier) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), multiplier);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos, double multiplier, double clamp) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), multiplier, clamp);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos) {
		return this.getWindMovement(pos, 1D);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double multiplier) {
		return this.getWindMovement(pos, multiplier, Double.MAX_VALUE);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double multiplier, double clamp) {
		return this.getWindMovement(pos, multiplier, clamp, 1D);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double multiplier, double clamp, double entityCausedWindMultiplier) {
		double brightness = this.level.getBrightness(LightLayer.SKY, BlockPos.containing(pos));
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Pair<Double, Vec3> entityWind = getEntityCausedWind(this.level, pos);
		double lerp = entityWind.getFirst();
		Vec3 entityCausedWind = entityWind.getSecond();
		double windX = Mth.lerp(lerp, this.windX * windMultiplier, entityCausedWind.x * entityCausedWindMultiplier) * multiplier;
		double windY = Mth.lerp(lerp, this.windY * windMultiplier, entityCausedWind.y * entityCausedWindMultiplier) * multiplier;
		double windZ = Mth.lerp(lerp, this.windZ * windMultiplier, entityCausedWind.z * entityCausedWindMultiplier) * multiplier;
		return new Vec3(
			Mth.clamp(windX, -clamp, clamp),
			Mth.clamp(windY, -clamp, clamp),
			Mth.clamp(windZ, -clamp, clamp)
		);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double multiplier, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), multiplier, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double multiplier, double clamp, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), multiplier, clamp, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double stretch) {
		return this.getWindMovement3D(pos, 1D, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double multiplier, double stretch) {
		return this.getWindMovement3D(pos, multiplier, Double.MAX_VALUE, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double multiplier, double clamp, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(Mth.clamp((wind.x()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z()) * multiplier, -clamp, clamp));
	}

	@NotNull
	private Vec3 sampleVec3(@NotNull ImprovedNoise sampler, double x, double y, double z) {
		if (!this.overrideWind) {
			double windX = sampler.noise(x, 0, 0);
			double windY = sampler.noise(0, y, 0);
			double windZ = sampler.noise(0, 0, z);
			return new Vec3(windX, windY, windZ);
		}
		return this.commandWind;
	}

	@NotNull
	private Vec3 sample3D(@NotNull Vec3 pos, double stretch) {
		double sampledTime = this.time * 0.1;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = this.perlinXoro.noise((xyz + sampledTime) * stretch, 0, 0);
		double windY = this.perlinXoro.noise(0, (xyz + sampledTime) * stretch, 0);
		double windZ = this.perlinXoro.noise(0, 0, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	private static final double ENTITY_WIND_SEARCH_RANGE = 5D;
	private static final double WIND_RANGE_BREEZE = 6D;
	private static final double WIND_RANGE_WIND_CHARGE = 5D;

	@NotNull
	public static Pair<Double, Vec3> getEntityCausedWind(@NotNull Level level, @NotNull Vec3 pos) {
		ArrayList<Pair<Double, Vec3>> winds = new ArrayList<>();
		Vec3 lowerCorner = pos.add(-ENTITY_WIND_SEARCH_RANGE, -ENTITY_WIND_SEARCH_RANGE, -ENTITY_WIND_SEARCH_RANGE);
		Vec3 upperCorner = pos.add(ENTITY_WIND_SEARCH_RANGE, ENTITY_WIND_SEARCH_RANGE, ENTITY_WIND_SEARCH_RANGE);

		List<? extends Entity> entities = level.getEntities(
			EntityTypeTest.forClass(Entity.class),
			new AABB(lowerCorner, upperCorner),
			EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS)
		);
		double strength = 0D;
		for (Entity entity : entities) {
			Vec3 entityPos = entity.position();
			double distance = pos.distanceTo(entityPos);
			if (entity instanceof Breeze) {
				if (distance <= WIND_RANGE_BREEZE) {
					Vec3 breezeLookVec = entity.getForward();
					Vec3 differenceInPoses = pos.subtract(entityPos);
					double scaledDistance = (WIND_RANGE_BREEZE - distance) / WIND_RANGE_BREEZE;
					double strengthFromDistance = Mth.clamp((WIND_RANGE_BREEZE - distance) / (WIND_RANGE_BREEZE * 0.75D), 0D, 1D);
					double angleBetween = AdvancedMath.getAngleBetweenXZ(breezeLookVec, differenceInPoses);

					double x = Math.cos((angleBetween * Math.PI) / 180D);
					double z = -Math.sin((angleBetween * Math.PI) / 180D);
					x = Mth.lerp(scaledDistance, (x - (differenceInPoses.x * 0.45D)) * 0.5D, x);
					z = Mth.lerp(scaledDistance, (z - (differenceInPoses.z * 0.45D)) * 0.5D, z);

					strength = Math.max(strength, strengthFromDistance);
					Vec3 windVec = new Vec3(x, strengthFromDistance, z);
					winds.add(new Pair<>(WIND_RANGE_BREEZE - distance, windVec));
				}
			} else if (entity instanceof AbstractWindCharge) {
				if (distance <= WIND_RANGE_WIND_CHARGE) {
					Vec3 chargeMovement = entity.getDeltaMovement();
					double strengthFromDistance = Mth.clamp((WIND_RANGE_WIND_CHARGE - distance) / (WIND_RANGE_WIND_CHARGE * 0.5D), 0D, 1D);
					strength = Math.max(strength, strengthFromDistance);
					Vec3 windVec = new Vec3(chargeMovement.x, chargeMovement.y, chargeMovement.z).scale(2D * strengthFromDistance);
					winds.add(new Pair<>((WIND_RANGE_WIND_CHARGE - distance) * 2D, windVec));
				}
			}
		}

		double finalX = 0D;
		double finalY = 0D;
		double finalZ = 0D;
		if (!winds.isEmpty()) {
			double x = 0D;
			double y = 0D;
			double z = 0D;
			double sumOfWeights = 0D;
			for (Pair<Double, Vec3> pair : winds) {
				double weight = pair.getFirst();
				sumOfWeights += weight;
				Vec3 windVec = pair.getSecond();
				x += weight * windVec.x;
				y += weight * windVec.y;
				z += weight * windVec.z;
			}
			finalX = x / sumOfWeights;
			finalY = y / sumOfWeights;
			finalZ = z / sumOfWeights;
		}

		return new Pair<>(strength, new Vec3(finalX, finalY, finalZ));
	}
}
