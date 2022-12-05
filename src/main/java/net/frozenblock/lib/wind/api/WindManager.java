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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;

public class WindManager {
	public static int tickCount;
	public static long time;
	public static double windX;
	public static double windY;
	public static double windZ;
	public static double laggedWindX;
	public static double laggedWindY;
	public static double laggedWindZ;
	public static double cloudX;
	public static double cloudY;
	public static double cloudZ;
	public static long seed = 0;

	public static void tick(MinecraftServer server, ServerLevel level) {
		++tickCount;
		//WIND
		float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		double calcTime = time * 0.0005;
		double calcTimeY = time * 0.00035;
		Vec3 vec3 = sampleVec3(perlinXoro, calcTime, calcTimeY, calcTime);
		windX = vec3.x + (vec3.x * thunderLevel);
		windY = vec3.y + (vec3.y * thunderLevel);
		windZ = vec3.z + (vec3.z * thunderLevel);
		//LAGGED WIND
		double calcLaggedTime = (time - 100) * 0.0005;
		double calcLaggedTimeY = (time - 140) * 0.00035;
		Vec3 laggedVec = sampleVec3(perlinXoro, calcLaggedTime, calcLaggedTimeY, calcLaggedTime);
		laggedWindX = laggedVec.x + (laggedVec.x * thunderLevel);
		laggedWindY = laggedVec.y + (laggedVec.y * thunderLevel);
		laggedWindZ = laggedVec.z + (laggedVec.z * thunderLevel);
		//CLOUDS
		cloudX += (laggedWindX * 0.025);
		cloudY += (laggedWindY * 0.01);
		cloudZ += (laggedWindZ * 0.025);
		//SYNC WITH CLIENTS IN CASE OF DESYNC
		if (tickCount >= 20) {
			tickCount = 0;
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeLong(server.overworld().getGameTime());
			byteBuf.writeDouble(WindManager.cloudX);
			byteBuf.writeDouble(WindManager.cloudY);
			byteBuf.writeDouble(WindManager.cloudZ);
			for (ServerPlayer player : PlayerLookup.all(server)) {
				ServerPlayNetworking.send(player, FrozenMain.SMALL_WIND_SYNC_PACKET, byteBuf);
			}
		}
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

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(windX * windMultiplier, windY * windMultiplier, windZ * windMultiplier);
	}

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3((windX * windMultiplier) * multiplier, (windY * windMultiplier) * multiplier, (windZ * windMultiplier) * multiplier);
	}

	public static Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier, double clamp) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(Mth.clamp((windX * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((windY * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((windZ * windMultiplier) * multiplier, -clamp, clamp));
	}

}
