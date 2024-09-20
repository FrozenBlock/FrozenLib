/*
 * Copyright (C) 2024 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.api;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WindManager {
	private static final long MIN_TIME_VALUE = Long.MIN_VALUE + 1;
	public static final Map<Function<WindManager, WindManagerExtension>, Integer> EXTENSION_PROVIDERS = new Object2ObjectOpenHashMap<>();
	private final List<WindDisturbance<?>> windDisturbancesA = new ArrayList<>();
	private final List<WindDisturbance<?>> windDisturbancesB = new ArrayList<>();
	private boolean isSwitchedServer;
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
	public long seed;
	private boolean seedSet = false;

	private final ServerLevel level;
	public ImprovedNoise noise;

	@SuppressWarnings("unchecked")
	public WindManager(@NotNull ServerLevel level) {
		this.level = level;
		this.noise = EasyNoiseSampler.createXoroNoise(this.seed);
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

	public void addWindDisturbanceAndSync(@NotNull WindDisturbance<?> windDisturbance) {
		Optional<WindDisturbancePacket> optionalPacket = windDisturbance.toPacket();
		if (optionalPacket.isPresent()) {
			for (ServerPlayer player : PlayerLookup.tracking(this.level, BlockPos.containing(windDisturbance.origin))) {
				ServerPlayNetworking.send(player, optionalPacket.get());
			}
		}
		this.addWindDisturbance(windDisturbance);
	}

	public void addWindDisturbance(@NotNull WindDisturbance<?> windDisturbance) {
		this.getWindDisturbanceStash().add(windDisturbance);
	}

	private List<WindDisturbance<?>> getWindDisturbances() {
		return !this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	private List<WindDisturbance<?>> getWindDisturbanceStash() {
		return this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	public void clearWindDisturbances() {
		this.getWindDisturbances().clear();
	}

	public void clearAllWindDisturbances() {
		this.getWindDisturbances().clear();
		this.getWindDisturbanceStash().clear();
	}

	public void clearAndSwitchWindDisturbances() {
		this.clearWindDisturbances();
		this.isSwitchedServer = !this.isSwitchedServer;
	}

	@NotNull
	public static WindManager getWindManager(@NotNull ServerLevel level) {
		return ((WindManagerInterface)level).frozenLib$getWindManager();
	}

	@NotNull
	public WindStorage createData() {
		return new WindStorage(this);
	}

	@NotNull
	public WindStorage createData(@NotNull CompoundTag nbt) {
		return WindStorage.load(nbt, this);
	}

	public void tick(@NotNull ServerLevel level) {
		if (!this.seedSet) {
			this.seedSet = true;
			this.seed = level.getSeed();
			this.noise = EasyNoiseSampler.createXoroNoise(this.seed);
		}
		this.runResetsIfNeeded();

		this.time += 1;
		//WIND
		float thunderLevel = this.level.getThunderLevel(1F) * 0.03F;
		double calcTime = this.time * 0.0005;
		double calcTimeY = this.time * 0.00035;
		Vec3 vec3 = sampleVec3(calcTime, calcTimeY, calcTime);
		this.windX = vec3.x + (vec3.x * thunderLevel);
		this.windY = vec3.y + (vec3.y * thunderLevel);
		this.windZ = vec3.z + (vec3.z * thunderLevel);
		//LAGGED WIND
		double calcLaggedTime = (this.time - 40) * 0.0005;
		double calcLaggedTimeY = (this.time - 60) * 0.00035;
		Vec3 laggedVec = sampleVec3(calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
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
	public Vec3 getWindMovement(@NotNull BlockPos pos, double scale) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos, double scale, double clamp) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale, clamp);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos) {
		return this.getWindMovement(pos, 1D);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double scale) {
		return this.getWindMovement(pos, scale, Double.MAX_VALUE);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double scale, double clamp) {
		return this.getWindMovement(pos, scale, clamp, 1D);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double scale, double clamp, double windDisturbanceScale) {
		double brightness = this.level.getBrightness(LightLayer.SKY, BlockPos.containing(pos));
		double windScale = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667D);
		Pair<Double, Vec3> disturbance = this.calculateWindDisturbance(level, pos);
		double disturbanceAmount = disturbance.getFirst();
		Vec3 windDisturbance = disturbance.getSecond();
		double windX = Mth.lerp(disturbanceAmount, this.windX * windScale, windDisturbance.x * windDisturbanceScale) * scale;
		double windY = Mth.lerp(disturbanceAmount, this.windY * windScale, windDisturbance.y * windDisturbanceScale) * scale;
		double windZ = Mth.lerp(disturbanceAmount, this.windZ * windScale, windDisturbance.z * windDisturbanceScale) * scale;

		if (FrozenLibConfig.IS_DEBUG) {
			FrozenNetworking.sendPacketToAllPlayers(
				this.level,
				new WindAccessPacket(pos)
			);
		}

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
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double clamp, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, clamp, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double stretch) {
		return this.getWindMovement3D(pos, 1D, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double scale, double stretch) {
		return this.getWindMovement3D(pos, scale, Double.MAX_VALUE, stretch);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double scale, double clamp, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(Mth.clamp((wind.x()) * scale, -clamp, clamp),
				Mth.clamp((wind.y()) * scale, -clamp, clamp),
				Mth.clamp((wind.z()) * scale, -clamp, clamp));
	}

	@NotNull
	private Vec3 sampleVec3(double x, double y, double z) {
		if (!this.overrideWind) {
			double windX = this.noise.noise(x, 0D, 0D);
			double windY = this.noise.noise(0D, y, 0D);
			double windZ = this.noise.noise(0D, 0D, z);
			return new Vec3(windX, windY, windZ);
		}
		return this.commandWind;
	}

	@NotNull
	private Vec3 sample3D(@NotNull Vec3 pos, double stretch) {
		double sampledTime = this.time * 0.1D;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = this.noise.noise((xyz + sampledTime) * stretch, 0D, 0D);
		double windY = this.noise.noise(0D, (xyz + sampledTime) * stretch, 0D);
		double windZ = this.noise.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	@NotNull
	private Pair<Double, Vec3> calculateWindDisturbance(@NotNull Level level, @NotNull Vec3 pos) {
		return calculateWindDisturbance(this.getWindDisturbances(), level, pos);
	}

	@NotNull
	public static Pair<Double, Vec3> calculateWindDisturbance(@NotNull List<WindDisturbance<?>> windDisturbances, @NotNull Level level, @NotNull Vec3 pos) {
		ArrayList<Pair<Double, Vec3>> winds = new ArrayList<>();
		double strength = 0D;
		for (WindDisturbance<?> windDisturbance : windDisturbances) {
			WindDisturbance.DisturbanceResult disturbanceResult = windDisturbance.calculateDisturbanceResult(level, pos);
			if (disturbanceResult.strength() != 0D && disturbanceResult.weight() != 0D) {
				strength = Math.max(strength, disturbanceResult.strength());
				winds.add(Pair.of(disturbanceResult.weight(), disturbanceResult.wind()));
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

		return Pair.of(strength, new Vec3(finalX, finalY, finalZ));
	}
}
