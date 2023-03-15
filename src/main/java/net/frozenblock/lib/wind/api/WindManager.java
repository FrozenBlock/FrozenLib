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

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.wind.impl.WindManagerInterface;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
	public boolean overrideWind;
	public long time;
	public Vec3 commandWind = Vec3.ZERO;
	public double windX;
	public double windY;
	public double windZ;
	public double laggedWindX;
	public double laggedWindY;
	public double laggedWindZ;
	public double cloudX;
	public double cloudY;
	public double cloudZ;
	public long seed = 0;

	private final ServerLevel level;

	public WindManager(ServerLevel level) {
		this.level = level;
		this.setSeed(level.getRandom().nextLong());
	}

	public void tick() {
		if (this.shouldReset()) {
			this.reset(this.level);
		}

		this.time = this.level.getGameTime();
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
		//CLOUDS
		this.cloudX += (this.laggedWindX * 0.025);
		this.cloudY += (this.laggedWindY * 0.01);
		this.cloudZ += (this.laggedWindZ * 0.025);
		//SYNC WITH CLIENTS IN CASE OF DESYNC
		if (this.time % 20 == 0) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeLong(this.time);
			byteBuf.writeDouble(this.cloudX);
			byteBuf.writeDouble(this.cloudY);
			byteBuf.writeDouble(this.cloudZ);
			for (ServerPlayer player : PlayerLookup.world(this.level)) {
				ServerPlayNetworking.send(player, FrozenMain.SMALL_WIND_SYNC_PACKET, byteBuf);
			}
		}
	}

	//Reset values in case of potential overflow
	private boolean shouldReset() {
		if (this.windX > Double.MAX_VALUE -10 || this.windX < Double.MIN_VALUE + 10) return true;
		if (this.windY > Double.MAX_VALUE -10 || this.windY < Double.MIN_VALUE + 10) return true;
		if (this.windZ > Double.MAX_VALUE -10 || this.windZ < Double.MIN_VALUE + 10) return true;
		if (this.laggedWindX > Double.MAX_VALUE -10 || this.laggedWindX < Double.MIN_VALUE + 10) return true;
		if (this.laggedWindY > Double.MAX_VALUE -10 || this.laggedWindY < Double.MIN_VALUE + 10) return true;
		if (this.laggedWindZ > Double.MAX_VALUE -10 || this.laggedWindZ < Double.MIN_VALUE + 10) return true;
		if (this.cloudX > Double.MAX_VALUE -10 || this.cloudX < Double.MIN_VALUE + 10) return true;
		if (this.cloudY > Double.MAX_VALUE -10 || this.cloudY < Double.MIN_VALUE + 10) return true;
		if (this.cloudZ > Double.MAX_VALUE -10 || this.cloudZ < Double.MIN_VALUE + 10) return true;
		return false;
	}

	private void reset(ServerLevel level) {
		this.windX = 0;
		this.windY = 0;
		this.windZ = 0;
		this.laggedWindX = 0;
		this.laggedWindY = 0;
		this.laggedWindZ = 0;
		this.cloudX = 0;
		this.cloudY = 0;
		this.cloudZ = 0;
		this.sendFullSync(level);
	}

	public FriendlyByteBuf createFullSyncByteBuf() {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(this.time);
		byteBuf.writeDouble(this.cloudX);
		byteBuf.writeDouble(this.cloudY);
		byteBuf.writeDouble(this.cloudZ);
		byteBuf.writeLong(this.seed);
		byteBuf.writeBoolean(this.overrideWind);
		byteBuf.writeDouble(this.commandWind.x());
		byteBuf.writeDouble(this.commandWind.y());
		byteBuf.writeDouble(this.commandWind.z());
		return byteBuf;
	}

	public void sendFullSync(ServerLevel level) {
		FriendlyByteBuf byteBuf = this.createFullSyncByteBuf();
		for (ServerPlayer player : PlayerLookup.world(level)) {
			this.sendFullSyncToPlayer(byteBuf, player);
		}
	}

	public void sendFullSyncToPlayer(FriendlyByteBuf byteBuf, ServerPlayer player) {
		ServerPlayNetworking.send(player, FrozenMain.WIND_SYNC_PACKET, byteBuf);
	}

	public Vec3 sampleVec3(ImprovedNoise sampler, double x, double y, double z) {
		if (!this.overrideWind) {
			double windX = sampler.noise(x, 0, 0);
			double windY = sampler.noise(0, y, 0);
			double windZ = sampler.noise(0, 0, z);
			return new Vec3(windX, windY, windZ);
		}
		return this.commandWind;
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

	public Vec3 getWindMovement(LevelReader reader, BlockPos pos) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(this.windX * windMultiplier, this.windY * windMultiplier, this.windZ * windMultiplier);
	}

	public Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3((this.windX * windMultiplier) * multiplier, (this.windY * windMultiplier) * multiplier, (this.windZ * windMultiplier) * multiplier);
	}

	public Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier, double clamp) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		return new Vec3(Mth.clamp((this.windX * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((this.windY * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((this.windZ * windMultiplier) * multiplier, -clamp, clamp));
	}

	public static WindManager getWindManager(ServerLevel level) {
		return ((WindManagerInterface)level).frozenLib$getWindManager();
	}

	public WindStorage createData() {
		return new WindStorage(this);
	}

	public WindStorage createData(CompoundTag nbt) {
		return this.createData().load(nbt);
	}

}
