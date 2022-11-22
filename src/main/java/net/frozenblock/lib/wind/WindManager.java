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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
	public static double cloudX;
	public static double cloudY;
	public static double cloudZ;
	public static long seed = 0;

	public static void tick(MinecraftServer server, ServerLevel level) {
		++tickCount;
		float thunderLevel = level.getThunderLevel(1F) * 0.03F;
		double calcTime = time * 0.0005;
		double calcTimeY = time * 0.00035;
		Vec3 vec3 = sampleVec3(perlinXoro, calcTime, calcTimeY, calcTime);
		windX = vec3.x + (vec3.x * thunderLevel);
		windY = vec3.y + (vec3.y * thunderLevel);
		windZ = vec3.z + (vec3.z * thunderLevel);
		cloudX += (windX * 0.025);
		cloudY += (windY * 0.005);
		cloudZ += (windZ * 0.025);
		if (tickCount >= 400) {
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
}
