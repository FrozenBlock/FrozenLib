/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

public class ClientWindManager {
	public static long time;
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
		double calcLaggedTime = (time - 100) * 0.0005;
		double calcLaggedTimeY = (time - 140) * 0.00035;
		Vec3 laggedVec = sampleVec3(perlinXoro, calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);
		//CLOUDS
		prevCloudX = cloudX;
		prevCloudY = cloudY;
		prevCloudZ = cloudZ;
		cloudX += (laggedWindX * 0.025);
		cloudY += (laggedWindY * 0.01);
		cloudZ += (laggedWindZ * 0.025);
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

	public static Vec3 sampleVec3(ImprovedNoise sampler, double x, double y, double z) {
		double windX = sampler.noise(x, 0, 0);
		double windY = sampler.noise(0, y, 0);
		double windZ = sampler.noise(0, 0, z);
		return new Vec3(windX, windY, windZ);
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

}
