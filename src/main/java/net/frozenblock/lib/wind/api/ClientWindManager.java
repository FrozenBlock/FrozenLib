/*
 * Copyright 2023 FrozenBlock
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.frozenlib_config.getter.FrozenLibConfigValues;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class ClientWindManager {
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

	public static double prevCloudX;
	public static double prevCloudY;
	public static double prevCloudZ;
	public static double cloudX;
	public static double cloudY;
	public static double cloudZ;

	public static long seed = 0;
	public static boolean hasInitialized;

	public static void tick(ClientLevel level) {
		float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		//WIND
		prevWindX = windX;
		prevWindY = windY;
		prevWindZ = windZ;
		time += 1;
		double calcTime = time * 0.0005;
		double calcTimeY = time * 0.00035;
		Vec3 vec3 = sampleVec3(perlinXoro, calcTime, calcTimeY, calcTime);
		windX = vec3.x + (vec3.x * thunderLevel);
		windY = vec3.y + (vec3.y * thunderLevel);
		windZ = vec3.z + (vec3.z * thunderLevel);

		//LAGGED WIND
		prevLaggedWindX = laggedWindX;
		prevLaggedWindY = laggedWindY;
		prevLaggedWindZ = laggedWindZ;
		double calcLaggedTime = (time - 40D) * 0.0005;
		double calcLaggedTimeY = (time - 60D) * 0.00035;
		Vec3 laggedVec = sampleVec3(perlinXoro, calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);

		//CLOUDS
		prevCloudX = cloudX;
		prevCloudY = cloudY;
		prevCloudZ = cloudZ;
		cloudX += (laggedWindX * 0.0055);
		cloudY += (laggedWindY * 0.01);
		cloudZ += (laggedWindZ * 0.0055);

		if (!hasInitialized && time > 80D && FrozenLibConfigValues.CONFIG.getter().useWindOnNonFrozenServers()) {
			RandomSource randomSource = AdvancedMath.random();
			setSeed(randomSource.nextLong());
			time = randomSource.nextLong();
			hasInitialized = true;
		}
	}

	public static Vec3 sampleVec3(ImprovedNoise sampler, double x, double y, double z) {
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

	public static ImprovedNoise perlinChecked = new ImprovedNoise(new LegacyRandomSource(seed));
	public static ImprovedNoise perlinLocal = new ImprovedNoise(new SingleThreadedRandomSource(seed));
	public static ImprovedNoise perlinXoro = new ImprovedNoise(new XoroshiroRandomSource(seed));

	public static void setSeed(long newSeed) {
		if (newSeed != seed) {
			seed = newSeed;
			perlinChecked = new ImprovedNoise(new LegacyRandomSource(seed));
			perlinLocal = new ImprovedNoise(new SingleThreadedRandomSource(seed));
			perlinXoro = new ImprovedNoise(new XoroshiroRandomSource(seed));
		}
	}

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		return shouldUseWind() ? new Vec3(windX * windMultiplier, windY * windMultiplier, windZ * windMultiplier) : Vec3.ZERO;
	}

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		return shouldUseWind() ? new Vec3((windX * windMultiplier) * multiplier, (windY * windMultiplier) * multiplier, (windZ * windMultiplier) * multiplier) : Vec3.ZERO;
	}

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier, double clamp) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		return shouldUseWind() ? new Vec3(Mth.clamp((windX * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((windY * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((windZ * windMultiplier) * multiplier, -clamp, clamp)) : Vec3.ZERO;
	}

	public static double getWindX(float partialTick) {
		return Mth.lerp(partialTick, prevWindX, windX);
	}

	public static double getWindY(float partialTick) {
		return Mth.lerp(partialTick, prevWindY, windY);
	}

	public static double getWindZ(float partialTick) {
		return Mth.lerp(partialTick, prevWindZ, windZ);
	}

	public static double getCloudX(float partialTick) {
		return Mth.lerp(partialTick, prevCloudX, cloudX);
	}

	public static double getCloudY(float partialTick) {
		return Mth.lerp(partialTick, prevCloudY, cloudY);
	}

	public static double getCloudZ(float partialTick) {
		return Mth.lerp(partialTick, prevCloudZ, cloudZ);
	}

	public static boolean shouldUseWind() {
		return hasInitialized || FrozenLibConfigValues.CONFIG.getter().useWindOnNonFrozenServers();
	}

	public Vec3 getWindMovement3D(LevelReader reader, BlockPos pos, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3(wind.x() * windMultiplier, wind.y() * windMultiplier, wind.z() * windMultiplier);
	}

	public Vec3 getWindMovement3D(LevelReader reader, BlockPos pos, double multiplier, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3((wind.x() * windMultiplier) * multiplier, (wind.y() * windMultiplier) * multiplier, (wind.z() * windMultiplier) * multiplier);
	}

	public Vec3 getWindMovement3D(LevelReader reader, BlockPos pos, double multiplier, double clamp, double stretch) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15D - brightness, 0D))), 0D) * 0.0667);
		Vec3 wind = this.sample3D(Vec3.atCenterOf(pos), stretch);
		return new Vec3(Mth.clamp((wind.x() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z() * windMultiplier) * multiplier, -clamp, clamp));
	}

	public Vec3 getWindMovement3D(Vec3 pos, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(wind.x(), wind.y(), wind.z());
	}

	public Vec3 getWindMovement3D(Vec3 pos, double multiplier, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3((wind.x()) * multiplier, (wind.y()) * multiplier, (wind.z()) * multiplier);
	}

	public Vec3 getWindMovement3D(Vec3 pos, double multiplier, double clamp, double stretch) {
		Vec3 wind = this.sample3D(pos, stretch);
		return new Vec3(Mth.clamp((wind.x()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z()) * multiplier, -clamp, clamp));
	}

	public Vec3 sample3D(Vec3 pos, double stretch) {
		double sampledTime = time * 0.1;
		double xyz = pos.x() + pos.y() + pos.z();
		double windX = perlinXoro.noise((xyz + sampledTime) * stretch, 0D, 0D);
		double windY = perlinXoro.noise(0D, (xyz + sampledTime) * stretch, 0D);
		double windZ = perlinXoro.noise(0D, 0D, (xyz + sampledTime) * stretch);
		return new Vec3(windX, windY, windZ);
	}
}
