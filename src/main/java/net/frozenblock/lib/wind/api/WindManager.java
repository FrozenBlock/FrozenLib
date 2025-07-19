/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.frozenblock.lib.wind.impl.networking.WindSyncPacket;
import net.minecraft.core.BlockPos;
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

/**
 * Handles wind on the server side.
 *
 * <p> One instance is created per {@link ServerLevel}.
 */
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

	/**
	 * Adds a {@link WindManagerExtension}.
	 *
	 * @param extension The {@link WindManagerExtension} to add.
	 * @param priority The priority of the added {@link WindManagerExtension}. The lower the value, the earlier it will run.
	 */
	public static void addExtension(Function<WindManager, WindManagerExtension> extension, int priority) {
		if (extension != null) EXTENSION_PROVIDERS.put(extension, priority);
	}

	/**
	 * Adds a {@link WindManagerExtension} with a priority of 1000.
	 *
	 * @param extension The {@link WindManagerExtension} to add.
	 */
	public static void addExtension(Function<WindManager, WindManagerExtension> extension) {
		addExtension(extension, 1000);
	}

	/**
	 * Adds a {@link WindDisturbance} to the world and syncs if with the client if possible.
	 *
	 * @param windDisturbance The {@link WindDisturbance} to add to the world and send to the client.
	 */
	public void addWindDisturbanceAndSync(@NotNull WindDisturbance<?> windDisturbance) {
		Optional<WindDisturbancePacket> optionalPacket = windDisturbance.toPacket();
		if (optionalPacket.isPresent()) {
			for (ServerPlayer player : PlayerLookup.world(this.level)) {
				if (windDisturbance.isWithinViewDistance(player.getChunkTrackingView())) {
					ServerPlayNetworking.send(player, optionalPacket.get());
				}
			}
		}
		this.addWindDisturbance(windDisturbance);
	}

	/**
	 * Adds a {@link WindDisturbance} to the world.
	 *
	 * @param windDisturbance The {@link WindDisturbance} to add.
	 */
	public void addWindDisturbance(@NotNull WindDisturbance<?> windDisturbance) {
		this.getWindDisturbanceStash().add(windDisturbance);
	}

	private List<WindDisturbance<?>> getWindDisturbances() {
		return !this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	private List<WindDisturbance<?>> getWindDisturbanceStash() {
		return this.isSwitchedServer ? this.windDisturbancesA : this.windDisturbancesB;
	}

	/**
	 * Clears all wind disturbances running on the current tick.
	 */
	public void clearWindDisturbances() {
		this.getWindDisturbances().clear();
	}

	/**
	 * Clears all wind disturbances running on the current tick, and the stash of wind disturbances to run the next tick.
	 */
	public void clearAllWindDisturbances() {
		this.getWindDisturbances().clear();
		this.getWindDisturbanceStash().clear();
	}

	/**
	 * Clears all wind disturbances running on the current tick, and replaces them with the stash of wind disturbances to run on the next tick.
	 */
	public void clearAndSwitchWindDisturbances() {
		this.clearWindDisturbances();
		this.isSwitchedServer = !this.isSwitchedServer;
	}

	/**
	 * Returns the {@link WindManager} used for a given {@link ServerLevel}.
	 *
	 * @param level The {@link ServerLevel} to obtain the {@link WindManager} for.
	 * @return the {@link WindManager} used for the given {@link ServerLevel}.
	 */
	@NotNull
	public static WindManager getWindManager(@NotNull ServerLevel level) {
		return ((WindManagerInterface)level).frozenLib$getWindManager();
	}

	@NotNull
	public SavedData.Factory<WindStorage> createData() {
		return new SavedData.Factory<>(
			() -> new WindStorage(this),
			(tag, provider) -> WindStorage.load(tag, this),
			DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
		);
	}

	public void tick(@NotNull ServerLevel level) {
		if (!this.seedSet) {
			this.seedSet = true;
			this.seed = level.getSeed();
			this.noise = EasyNoiseSampler.createXoroNoise(this.seed);
		}
		if (level.tickRateManager().runsNormally()) {
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
	}

	/**
	 * Resets the values in the rare case of an overflow.
	 *
	 * @return whether the values were reset this tick.
	 */
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
	public WindSyncPacket createSyncPacket() {
		return new WindSyncPacket(
			this.time,
			this.seed,
			this.overrideWind,
			this.commandWind
		);
	}

	public void sendSync(@NotNull ServerLevel level) {
		WindSyncPacket packet = this.createSyncPacket();
		for (ServerPlayer player : PlayerLookup.world(level)) {
			this.sendSyncToPlayer(packet, player);
		}
	}

	public void sendSyncToPlayer(@NotNull WindSyncPacket packet, @NotNull ServerPlayer player) {
		ServerPlayNetworking.send(player, packet);
		for (WindManagerExtension extension : this.attachedExtensions) {
			ServerPlayNetworking.send(player, extension.syncPacket(packet));
		}
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @return the wind movement at the center of the specified {@link BlockPos}.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos));
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos, double scale) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale);
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied and clamped.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied and clamped.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull BlockPos pos, double scale, double clamp) {
		return this.getWindMovement(Vec3.atBottomCenterOf(pos), scale, clamp);
	}

	/**
	 * Returns the wind movement at the center of a specified {@link Vec3}.
	 *
	 * @param pos The {@link Vec3} to check.
	 * @return the wind movement at the specified {@link Vec3}.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos) {
		return this.getWindMovement(pos, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied.
	 *
	 * @param pos The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double scale) {
		return this.getWindMovement(pos, scale, Double.MAX_VALUE);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied and clamped.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied and clamped.
	 */
	@NotNull
	public Vec3 getWindMovement(@NotNull Vec3 pos, double scale, double clamp) {
		return this.getWindMovement(pos, scale, clamp, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 *
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @param windDisturbanceScale Multiplies the wind disturbance value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 */
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

	@Deprecated
	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), stretch);
	}

	@Deprecated
	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, stretch);
	}

	@Deprecated
	@NotNull
	public Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double clamp, double stretch) {
		return this.getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, clamp, stretch);
	}

	@Deprecated
	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double stretch) {
		return this.getWindMovement3D(pos, 1D, stretch);
	}

	@Deprecated
	@NotNull
	public Vec3 getWindMovement3D(@NotNull Vec3 pos, double scale, double stretch) {
		return this.getWindMovement3D(pos, scale, Double.MAX_VALUE, stretch);
	}

	@Deprecated
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

	@Deprecated
	@NotNull
	private Vec3 sample3D(@NotNull Vec3 pos, double stretch) {
		double sampledTime = this.time * 0.1D;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = this.noise.noise((xyz + sampledTime) * stretch, 0D, 0D);
		double windY = this.noise.noise(0D, (xyz + sampledTime) * stretch, 0D);
		double windZ = this.noise.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	/**
	 * Calculates the strength and movement of the current {@link WindDisturbance}s at a given position.
	 *
	 * @param level The provided {@link Level}.
	 * @param pos The {@link Vec3} being checked.
	 * @return the strength and movement of the current {@link WindDisturbance}s at a given position.
	 */
	@NotNull
	private Pair<Double, Vec3> calculateWindDisturbance(@NotNull Level level, @NotNull Vec3 pos) {
		return calculateWindDisturbance(this.getWindDisturbances(), level, pos);
	}

	/**
	 * Calculates the strength and movement out of a provided list of {@link WindDisturbance}s at a given position.
	 *
	 * @param windDisturbances The list of {@link WindDisturbance}s to calculate from.
	 * @param level The provided {@link Level}.
	 * @param pos The {@link Vec3} being checked.
	 * @return the strength and movement out of a provided list of {@link WindDisturbance}s at a given position.
	 */
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
