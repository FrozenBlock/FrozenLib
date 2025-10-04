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

package net.frozenblock.lib.wind.client.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.core.client.api.FrustumUtil;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.client.api.ClientWindManagerExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Handles wind on the client side.
 */
@Environment(EnvType.CLIENT)
public final class ClientWindManager {
	public static final List<ClientWindManagerExtension> EXTENSIONS = new ObjectArrayList<>();
	private static final List<WindDisturbance<?>> WIND_DISTURBANCES_A = new ArrayList<>();
	private static final List<WindDisturbance<?>> WIND_DISTURBANCES_B = new ArrayList<>();
	private static boolean isSwitched;

	public static List<WindDisturbance<?>> getWindDisturbances() {
		return !isSwitched ? WIND_DISTURBANCES_A : WIND_DISTURBANCES_B;
	}

	public static List<WindDisturbance<?>> getWindDisturbanceStash() {
		return isSwitched ? WIND_DISTURBANCES_A : WIND_DISTURBANCES_B;
	}

	public static void clearWindDisturbances() {
		getWindDisturbances().clear();
	}

	public static void clearAllWindDisturbances() {
		getWindDisturbances().clear();
		getWindDisturbanceStash().clear();
	}

	public static void clearAndSwitchWindDisturbances() {
		clearWindDisturbances();
		isSwitched = !isSwitched;
	}

	public synchronized static void addWindDisturbance(@NotNull WindDisturbance<?> windDisturbance) {
		getWindDisturbanceStash().add(windDisturbance);
	}

	public static long time;
	public static boolean overrideWind;
	public static Vec3 commandWind = Vec3.ZERO;

	public static double prevWindX;
	public static double prevWindY;
	public static double prevWindZ;
	public static double windX;
	public static double windY;
	public static double windZ;

	public static double prevLaggedWindX;
	public static double prevLaggedWindY;
	public static double prevLaggedWindZ;
	public static double laggedWindX;
	public static double laggedWindY;
	public static double laggedWindZ;
	public static boolean hasInitialized;

	/**
	 * Adds a {@link ClientWindManagerExtension}.
	 *
	 * @param extension the {@link ClientWindManagerExtension} to add.
	 */
	public static void addExtension(@Nullable Supplier<ClientWindManagerExtension> extension) {
		if (extension != null) addExtension(extension.get());
	}

	/**
	 * Adds a {@link ClientWindManagerExtension}.
	 *
	 * @param extension the {@link ClientWindManagerExtension} to add.
	 */
	public static void addExtension(@Nullable ClientWindManagerExtension extension) {
		if (extension != null) EXTENSIONS.add(extension);
	}

	public static ImprovedNoise noise = EasyNoiseSampler.createXoroNoise(0L);

	public static void setSeed(long seed) {
		noise = EasyNoiseSampler.createXoroNoise(seed);
	}

	/**
	 * @return the current global wind's lerped X value.
	 */
	public static double getWindX(float partialTick) {
		return Mth.lerp(partialTick, prevWindX, windX);
	}

	/**
	 * @return the current global wind's lerped Y value.
	 */
	public static double getWindY(float partialTick) {
		return Mth.lerp(partialTick, prevWindY, windY);
	}

	/**
	 * @return the current global wind's lerped Z value.
	 */
	public static double getWindZ(float partialTick) {
		return Mth.lerp(partialTick, prevWindZ, windZ);
	}

	/**
	 * @return whether wind is currently enabled.
	 */
	public static boolean shouldUseWind() {
		return hasInitialized || FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS;
	}

	@ApiStatus.Internal
	public static void tick(@NotNull ClientLevel level) {
		Debug.tick(level);

		if (!level.tickRateManager().runsNormally()) return;
		float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		//WIND
		prevWindX = windX;
		prevWindY = windY;
		prevWindZ = windZ;
		time += 1;
		double calcTime = time * 0.0005D;
		double calcTimeY = time * 0.00035D;
		Vec3 vec3 = sampleVec3(noise, calcTime, calcTimeY, calcTime);
		windX = vec3.x + (vec3.x * thunderLevel);
		windY = vec3.y + (vec3.y * thunderLevel);
		windZ = vec3.z + (vec3.z * thunderLevel);

		//LAGGED WIND
		prevLaggedWindX = laggedWindX;
		prevLaggedWindY = laggedWindY;
		prevLaggedWindZ = laggedWindZ;
		double calcLaggedTime = (time - 40D) * 0.0005D;
		double calcLaggedTimeY = (time - 60D) * 0.00035D;
		Vec3 laggedVec = sampleVec3(noise, calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);

		// EXTENSIONS
		for (ClientWindManagerExtension extension : EXTENSIONS) {
			extension.baseTick();
			extension.clientTick();
		}

		if (!hasInitialized && FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS) {
			hasInitialized = true;
			RandomSource randomSource = AdvancedMath.random();
			noise = EasyNoiseSampler.createXoroNoise(randomSource.nextLong());
			time = randomSource.nextLong();
		}
	}

	@ApiStatus.Internal
	public static void reset() {
		hasInitialized = false;
		overrideWind = false;
		commandWind = Vec3.ZERO;

		time = 0L;

		prevWindX = 0D;
		prevWindY = 0D;
		prevWindZ = 0D;
		windX = 0D;
		windY = 0D;
		windZ = 0D;

		prevLaggedWindX = 0D;
		prevLaggedWindY = 0D;
		prevLaggedWindZ = 0D;
		laggedWindX = 0D;
		laggedWindY = 0D;
		laggedWindZ = 0D;

		isSwitched = false;
		clearAllWindDisturbances();
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull BlockPos pos) {
		return getWindMovement(level, Vec3.atBottomCenterOf(pos));
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull BlockPos pos, double scale) {
		return getWindMovement(level, Vec3.atBottomCenterOf(pos), scale);
	}

	/**
	 * Returns the wind movement at the bottom center of a specified {@link BlockPos}, multiplied and clamped.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the bottom center of the specified {@link BlockPos}, multiplied and clamped.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull BlockPos pos, double scale, double clamp) {
		return getWindMovement(level, Vec3.atBottomCenterOf(pos), scale, clamp);
	}

	/**
	 * Returns the wind movement at the center of a specified {@link Vec3}.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link Vec3} to check.
	 * @return the wind movement at the specified {@link Vec3}.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull Vec3 pos) {
		return getWindMovement(level, pos, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link Vec3} to check.
	 * @param scale Multiplies the returned value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull Vec3 pos, double scale) {
		return getWindMovement(level, pos, scale, Double.MAX_VALUE);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied and clamped.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied and clamped.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull Vec3 pos, double scale, double clamp) {
		return getWindMovement(level, pos, scale, clamp, 1D);
	}

	/**
	 * Returns the wind movement at a specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @param scale Multiplies the returned value.
	 * @param clamp Clamps the returned value between the negative and positive versions of this value.
	 * @param windDisturbanceScale Multiplies the wind disturbance value.
	 * @return the wind movement at the specified {@link Vec3}, multiplied, clamped, and with a separately multiplied wind disturbance value.
	 */
	@NotNull
	public static Vec3 getWindMovement(@NotNull Level level, @NotNull Vec3 pos, double scale, double clamp, double windDisturbanceScale) {
		if (!shouldUseWind()) return Vec3.ZERO;

		double brightness = level.getBrightness(LightLayer.SKY, BlockPos.containing(pos));
		double windScale = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667D);
		Pair<Double, Vec3> disturbance = WindManager.calculateWindDisturbance(getWindDisturbances(), level, pos);
		double disturbanceAmount = disturbance.getFirst();
		Vec3 windDisturbance = disturbance.getSecond();
		double newWindX = Mth.lerp(disturbanceAmount, windX * windScale, windDisturbance.x * windDisturbanceScale) * scale;
		double newWindY = Mth.lerp(disturbanceAmount, windY * windScale, windDisturbance.y * windDisturbanceScale) * scale;
		double newWindZ = Mth.lerp(disturbanceAmount, windZ * windScale, windDisturbance.z * windDisturbanceScale) * scale;

		if (FrozenLibConstants.DEBUG_WIND) Debug.addAccessedPosition(pos);

		return new Vec3(
			Mth.clamp(newWindX, -clamp, clamp),
			Mth.clamp(newWindY, -clamp, clamp),
			Mth.clamp(newWindZ, -clamp, clamp)
		);
	}

	/**
	 * Returns only the wind disturbance at the specified {@link Vec3}.
	 *
	 * @param level The {@link Level} to read from.
	 * @param pos The {@link BlockPos} to check.
	 * @return the wind disturbance at the specified {@link Vec3}.
	 */
	@NotNull
	public static Vec3 getRawDisturbanceMovement(@NotNull Level level, @NotNull Vec3 pos) {
		Pair<Double, Vec3> disturbance = WindManager.calculateWindDisturbance(getWindDisturbances(), level, pos);
		double disturbanceAmount = disturbance.getFirst();
		Vec3 windDisturbance = disturbance.getSecond();
		double newWindX = Mth.lerp(disturbanceAmount, 0D, windDisturbance.x);
		double newWindY = Mth.lerp(disturbanceAmount, 0D, windDisturbance.y);
		double newWindZ = Mth.lerp(disturbanceAmount, 0D, windDisturbance.z);
		return new Vec3(
			newWindX,
			newWindY,
			newWindZ
		);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull BlockPos pos, double stretch) {
		return getWindMovement3D(Vec3.atBottomCenterOf(pos), stretch);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double stretch) {
		return getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, stretch);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull BlockPos pos, double scale, double clamp, double stretch) {
		return getWindMovement3D(Vec3.atBottomCenterOf(pos), scale, clamp, stretch);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull Vec3 pos, double stretch) {
		return getWindMovement3D(pos, 1D, stretch);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull Vec3 pos, double scale, double stretch) {
		return getWindMovement3D(pos, scale, Double.MAX_VALUE, stretch);
	}

	@Deprecated
	@NotNull
	public static Vec3 getWindMovement3D(@NotNull Vec3 pos, double scale, double clamp, double stretch) {
		Vec3 wind = sample3D(pos, stretch);
		return new Vec3(Mth.clamp((wind.x()) * scale, -clamp, clamp),
			Mth.clamp((wind.y()) * scale, -clamp, clamp),
			Mth.clamp((wind.z()) * scale, -clamp, clamp));
	}

	@NotNull
	public static Vec3 sampleVec3(@NotNull ImprovedNoise sampler, double x, double y, double z) {
		if (shouldUseWind()) {
			if (!overrideWind) {
				double windX = sampler.noise(x, 0D, 0D);
				double windY = sampler.noise(0D, y, 0D);
				double windZ = sampler.noise(0D, 0D, z);
				return new Vec3(windX, windY, windZ);
			}
			return commandWind;
		} else {
			return Vec3.ZERO;
		}
	}

	@Deprecated
	@NotNull
	public static Vec3 sample3D(@NotNull Vec3 pos, double stretch) {
		if (!shouldUseWind()) return Vec3.ZERO;

		double sampledTime = time * 0.1D;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = noise.noise((xyz + sampledTime) * stretch, 0D, 0D);
		double windY = noise.noise(0D, (xyz + sampledTime) * stretch, 0D);
		double windZ = noise.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	@VisibleForDebug
	public static class Debug {
		private static final List<Vec3> ACCESSED_POSITIONS = new ArrayList<>();
		private static final List<WindDisturbance<?>> WIND_DISTURBANCES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_NODES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_DISTURBANCE_NODES = new ArrayList<>();

		public static void tick(ClientLevel level) {
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();

			if (FrozenLibConstants.DEBUG_WIND) DEBUG_NODES.addAll(createWindNodes(level));
			if (FrozenLibConstants.DEBUG_WIND_DISTURBANCES) {
				WIND_DISTURBANCES.addAll(getWindDisturbances());
				DEBUG_DISTURBANCE_NODES.addAll(createWindDisturbanceNodes(level));
			}

			ACCESSED_POSITIONS.clear();
		}

		@VisibleForDebug
		public static void addAccessedPosition(Vec3 vec3) {
			ACCESSED_POSITIONS.add(vec3);
		}

		@VisibleForDebug
		public static void clear() {
			ACCESSED_POSITIONS.clear();
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();
		}

		@VisibleForDebug
		public static @NotNull List<List<Pair<Vec3, Integer>>> getDebugNodes() {
			return DEBUG_NODES;
		}

		private static @NotNull List<List<Pair<Vec3, Integer>>> createWindNodes(ClientLevel level) {
			List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			ACCESSED_POSITIONS.forEach(
				vec3 -> {
					if (!FrustumUtil.isVisible(vec3, 0.5D)) return;
					windNodes.add(createWindNodes(level, vec3, 1.5D, false));
				}
			);

			return windNodes;
		}

		@VisibleForDebug
		public static @NotNull @Unmodifiable List<WindDisturbance<?>> getWindDisturbances() {
			return ImmutableList.copyOf(WIND_DISTURBANCES);
		}

		@VisibleForDebug
		public static @NotNull List<List<Pair<Vec3, Integer>>> getDebugDisturbanceNodes() {
			return DEBUG_DISTURBANCE_NODES;
		}

		private static @NotNull List<List<Pair<Vec3, Integer>>> createWindDisturbanceNodes(ClientLevel level) {
			List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			WIND_DISTURBANCES.forEach(
				windDisturbance -> {
					if (FrustumUtil.isVisible(windDisturbance.affectedArea)) {
						BlockPos.betweenClosed(
							BlockPos.containing(windDisturbance.affectedArea.getMinPosition()),
							BlockPos.containing(windDisturbance.affectedArea.getMaxPosition())
						).forEach(
							blockPos -> {
								Vec3 blockPosCenter = Vec3.atCenterOf(blockPos);
								windNodes.add(createWindNodes(level, blockPosCenter, 1D, true));
							}
						);
					}
				}
			);
			return windNodes;
		}

		private static @NotNull List<Pair<Vec3, Integer>> createWindNodes(Level level, Vec3 origin, double stretch, boolean disturbanceOnly) {
			List<Pair<Vec3, Integer>> windNodes = new ArrayList<>();
			Vec3 wind = disturbanceOnly ?
				ClientWindManager.getRawDisturbanceMovement(level, origin)
				: ClientWindManager.getWindMovement(level, origin);

			final double windlength = wind.length();
			if (windlength != 0D) {
				int increments = 3;
				Vec3 lineStart = origin;
				double windLineScale = (1D / increments) * stretch;
				windNodes.add(
					Pair.of(
						lineStart,
						calculateNodeColor(Math.min(1D, windlength), disturbanceOnly)
					)
				);

				for (int i = 0; i < increments; ++i) {
					Vec3 lineEnd = lineStart.add(wind.scale(windLineScale));
					windNodes.add(
						Pair.of(
							lineEnd,
							calculateNodeColor(Math.min(1D, windlength), disturbanceOnly)
						)
					);
					lineStart = lineEnd;
					wind = disturbanceOnly ?
						ClientWindManager.getRawDisturbanceMovement(level, lineStart)
						: ClientWindManager.getWindMovement(level, lineStart);
				}
			}

			return windNodes;
		}

		private static int calculateNodeColor(double strength, boolean disturbanceOnly) {
			return ARGB.color(
				255,
				(int) Mth.lerp(strength, 255, 0),
				(int) Mth.lerp(strength, 90, 255),
				disturbanceOnly ? 0 : 255
			);
		}
	}
}
