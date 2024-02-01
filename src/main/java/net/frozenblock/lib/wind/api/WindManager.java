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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.saveddata.SavedData;
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
	public Vec3 getWindMovement(@NotNull LevelReader reader, @NotNull BlockPos pos) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(this.windX * windMultiplier, this.windY * windMultiplier, this.windZ * windMultiplier);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull LevelReader reader, @NotNull BlockPos pos, double multiplier) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3((this.windX * windMultiplier) * multiplier, (this.windY * windMultiplier) * multiplier, (this.windZ * windMultiplier) * multiplier);
	}

	@NotNull
	public Vec3 getWindMovement(@NotNull LevelReader reader, @NotNull BlockPos pos, double multiplier, double clamp) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(Mth.clamp((this.windX * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((this.windY * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((this.windZ * windMultiplier) * multiplier, -clamp, clamp));
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull LevelReader reader, @NotNull BlockPos pos, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3(wind.x() * windMultiplier, wind.y() * windMultiplier, wind.z() * windMultiplier);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull LevelReader reader, @NotNull BlockPos pos, double multiplier, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3((wind.x() * windMultiplier) * multiplier, (wind.y() * windMultiplier) * multiplier, (wind.z() * windMultiplier) * multiplier);
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull LevelReader reader, @NotNull BlockPos pos, double multiplier, double clamp, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3(Mth.clamp((wind.x() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z() * windMultiplier) * multiplier, -clamp, clamp));
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(wind.x(), wind.y(), wind.z());
	}

	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double multiplier, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3((wind.x()) * multiplier, (wind.y()) * multiplier, (wind.z()) * multiplier);
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
		double sampledTime = time * 0.1;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = this.perlinXoro.noise((xyz + sampledTime) * stretch, 0, 0);
		double windY = this.perlinXoro.noise(0, (xyz + sampledTime) * stretch, 0);
		double windZ = this.perlinXoro.noise(0, 0, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}
}
