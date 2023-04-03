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

package net.frozenblock.lib.wind.api.wind3d;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.wind.impl.Wind3DStorage;
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

public class WindManager3D {
	private static final long MIN_TIME_VALUE = Long.MIN_VALUE + 1;

	public long time;
	public long seed = 0;

	private final ServerLevel level;

	public WindManager3D(ServerLevel level) {
		this.level = level;
		this.setSeed(level.getRandom().nextLong());
	}

	public void tick() {
		this.runResetsIfNeeded();
		this.time += 1;
		//SYNC WITH CLIENTS IN CASE OF DESYNC
		if (this.time % 20 == 0) {
			this.sendSync(this.level);
		}
	}

	//Reset values in case of potential overflow
	private boolean runResetsIfNeeded() {
		boolean needsReset = false;
		if (this.time == Long.MAX_VALUE || this.time == Long.MIN_VALUE) {
			needsReset = true;
			this.time = MIN_TIME_VALUE;
		}
		if (needsReset) {
			this.sendSync(this.level);
		}
		return needsReset;
	}

	public FriendlyByteBuf createSyncByteBuf() {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(this.time);
		byteBuf.writeLong(this.seed);
		return byteBuf;
	}

	public void sendSync(ServerLevel level) {
		FriendlyByteBuf byteBuf = this.createSyncByteBuf();
		for (ServerPlayer player : PlayerLookup.world(level)) {
			this.sendSyncToPlayer(byteBuf, player);
		}
	}

	public void sendSyncToPlayer(FriendlyByteBuf byteBuf, ServerPlayer player) {
		ServerPlayNetworking.send(player, FrozenMain.WIND_3D_SYNC_PACKET, byteBuf);
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
		Vec3 wind = this.sample(Vec3.atCenterOf(pos));
		return new Vec3(wind.x() * windMultiplier, wind.y() * windMultiplier, wind.z() * windMultiplier);
	}

	public Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Vec3 wind = this.sample(Vec3.atCenterOf(pos));
		return new Vec3((wind.x() * windMultiplier) * multiplier, (wind.y() * windMultiplier) * multiplier, (wind.z() * windMultiplier) * multiplier);
	}

	public Vec3 getWindMovement(LevelReader reader, BlockPos pos, double multiplier, double clamp) {
		double brightness = reader.getBrightness(LightLayer.SKY, pos);
		double windMultiplier = (Math.max((brightness - (Math.max(15 - brightness, 0))), 0) * 0.0667);
		Vec3 wind = this.sample(Vec3.atCenterOf(pos));
		return new Vec3(Mth.clamp((wind.x() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y() * windMultiplier) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z() * windMultiplier) * multiplier, -clamp, clamp));
	}

	public Vec3 getWindMovement(Vec3 pos) {
		Vec3 wind = this.sample(pos);
		return new Vec3(wind.x(), wind.y(), wind.z());
	}

	public Vec3 getWindMovement(Vec3 pos, double multiplier) {
		Vec3 wind = this.sample(pos);
		return new Vec3((wind.x()) * multiplier, (wind.y()) * multiplier, (wind.z()) * multiplier);
	}

	public Vec3 getWindMovement(Vec3 pos, double multiplier, double clamp) {
		Vec3 wind = this.sample(pos);
		return new Vec3(Mth.clamp((wind.x()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.y()) * multiplier, -clamp, clamp),
				Mth.clamp((wind.z()) * multiplier, -clamp, clamp));
	}

	public Vec3 sample(Vec3 pos) {
		double windX = this.perlinXoro.noise((pos.x() + this.time) * 0.025, 0, 0);
		double windY = this.perlinXoro.noise(0, (pos.y() + this.time) * 0.025, 0);
		double windZ = this.perlinXoro.noise(0, 0, (pos.z() + this.time) * 0.025);
		return new Vec3(windX, windY, windZ);
	}

	public static WindManager3D getWindManager3D(ServerLevel level) {
		return ((WindManagerInterface)level).frozenLib$getWindManager3D();
	}

	public Wind3DStorage createData() {
		return new Wind3DStorage(this);
	}

	public Wind3DStorage createData(CompoundTag nbt) {
		return this.createData().load(nbt);
	}

}
