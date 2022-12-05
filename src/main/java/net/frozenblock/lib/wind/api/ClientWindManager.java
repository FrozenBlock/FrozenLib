/*
 * Copyright 2022 FrozenBlock
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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

public class ClientWindManager extends WindManager {
	public static double prevWindX;
	public static double prevWindY;
	public static double prevWindZ;

	public static double prevLaggedWindX;
	public static double prevLaggedWindY;
	public static double prevLaggedWindZ;

	public static double prevCloudX;
	public static double prevCloudY;
	public static double prevCloudZ;

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

}
