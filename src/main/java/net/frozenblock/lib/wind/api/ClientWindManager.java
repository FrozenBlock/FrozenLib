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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;
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

	public static void tick(@NotNull ClientLevel level) {
		if (level.tickRateManager().runsNormally()) {
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

			if (!hasInitialized && time > 80D && FrozenLibConfig.USE_WIND_ON_NON_FROZEN_SERVERS) {
				RandomSource randomSource = AdvancedMath.random();
				noise = EasyNoiseSampler.createXoroNoise(randomSource.nextLong());
				time = randomSource.nextLong();
				hasInitialized = true;
			}
		}
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
		double brightness = level.getBrightness(LightLayer.SKY, BlockPos.containing(pos));
		double windScale = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667D);
		Pair<Double, Vec3> disturbance = WindManager.calculateWindDisturbance(getWindDisturbances(), level, pos);
		double disturbanceAmount = disturbance.getFirst();
		Vec3 windDisturbance = disturbance.getSecond();
		double newWindX = Mth.lerp(disturbanceAmount, windX * windScale, windDisturbance.x * windDisturbanceScale) * scale;
		double newWindY = Mth.lerp(disturbanceAmount, windY * windScale, windDisturbance.y * windDisturbanceScale) * scale;
		double newWindZ = Mth.lerp(disturbanceAmount, windZ * windScale, windDisturbance.z * windDisturbanceScale) * scale;

		if (FrozenLibConfig.IS_DEBUG) {
			addAccessedPosition(pos);
		}

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
		double sampledTime = time * 0.1D;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = noise.noise((xyz + sampledTime) * stretch, 0D, 0D);
		double windY = noise.noise(0D, (xyz + sampledTime) * stretch, 0D);
		double windZ = noise.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}

	private static final List<Vec3> ACCESSED_POSITIONS = new ArrayList<>();

	@VisibleForDebug
	public static void addAccessedPosition(Vec3 vec3) {
		ACCESSED_POSITIONS.add(vec3);
	}

	@VisibleForDebug
	public static @NotNull @Unmodifiable List<Vec3> getAccessedPositions() {
		return ImmutableList.copyOf(ACCESSED_POSITIONS);
	}

	@VisibleForDebug
	public static void clearAccessedPositions() {
		ACCESSED_POSITIONS.clear();
	}
}
