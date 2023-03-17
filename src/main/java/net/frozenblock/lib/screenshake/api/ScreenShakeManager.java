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

package net.frozenblock.lib.screenshake.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.ScreenShakeManagerInterface;
import net.frozenblock.lib.screenshake.impl.ScreenShakeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ScreenShakeManager {
	private final ArrayList<ScreenShake> shakes = new ArrayList<>();
	private final ServerLevel level;

	public ScreenShakeManager(ServerLevel level) {
		this.level = level;
	}

	public void tick() {
		this.getShakes().removeIf(ScreenShake::shouldRemove);
		for (ScreenShake shake : this.getShakes()) {
			if (this.level.getChunkSource().hasChunk(shake.chunkPos.x, shake.chunkPos.z)) {
				shake.ticks += 1;
				Collection<ServerPlayer> playersTrackingChunk = PlayerLookup.tracking(this.level, shake.chunkPos);
				for (ServerPlayer serverPlayer : playersTrackingChunk) {
					if (!shake.trackingPlayers.contains(serverPlayer)) {
						ScreenShakeManager.sendScreenShakePacketTo(serverPlayer, shake.getIntensity(), shake.getDuration(), shake.getDurationFalloffStart(), shake.getPos().x(), shake.getPos().y(), shake.getPos().z(), shake.getMaxDistance(), shake.getTicks());
					}
				}
				shake.trackingPlayers.clear();
				shake.trackingPlayers.addAll(playersTrackingChunk);
			}
		}
	}

	public void addShake(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		this.getShakes().add(new ScreenShake(intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public ArrayList<ScreenShake> getShakes() {
		return this.shakes;
	}

	public void load(CompoundTag nbt) {
		if (nbt.contains("ScreenShakes", 9)) {
			this.getShakes().clear();
			DataResult<List<ScreenShake>> var10000 = ScreenShake.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getList("ScreenShakes", 10)));
			Logger var10001 = FrozenMain.LOGGER4;
			Objects.requireNonNull(var10001);
			Optional<List<ScreenShake>> list = var10000.resultOrPartial(var10001::error);
			list.ifPresent(this.getShakes()::addAll);
		}
	}

	public void save(CompoundTag nbt) {
		DataResult<Tag> var10000 = ScreenShake.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.shakes);
		Logger var10001 = FrozenMain.LOGGER4;
		Objects.requireNonNull(var10001);
		var10000.resultOrPartial(var10001::error).ifPresent((cursorsNbt) -> nbt.put("ScreenShakes", cursorsNbt));
	}

	public static class ScreenShake {
		private final float intensity;
		public final int duration;
		private final int durationFalloffStart;
		protected Vec3 pos;
		public final float maxDistance;
		public int ticks;

		public ArrayList<ServerPlayer> trackingPlayers = new ArrayList<>();
		public final ChunkPos chunkPos;

		public static final Codec<ScreenShake> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				Codec.FLOAT.fieldOf("Intensity").forGetter(ScreenShake::getIntensity),
				Codec.INT.fieldOf("Duration").forGetter(ScreenShake::getDuration),
				Codec.INT.fieldOf("FalloffStart").forGetter(ScreenShake::getDurationFalloffStart),
				Vec3.CODEC.fieldOf("Position").forGetter(ScreenShake::getPos),
				Codec.FLOAT.fieldOf("MaxDistance").forGetter(ScreenShake::getMaxDistance),
				Codec.INT.fieldOf("Ticks").forGetter(ScreenShake::getTicks)
		).apply(instance, ScreenShake::new));

		public ScreenShake(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
			this.chunkPos = new ChunkPos(new BlockPos(pos));
		}

		public boolean shouldRemove() {
			return this.ticks > this.duration;
		}

		public float getIntensity() {
			return this.intensity;
		}

		public int getDuration() {
			return this.duration;
		}

		public int getDurationFalloffStart() {
			return this.durationFalloffStart;
		}

		public Vec3 getPos() {
			return this.pos;
		}

		public float getMaxDistance() {
			return this.maxDistance;
		}

		public int getTicks() {
			return this.ticks;
		}

	}

	public static ScreenShakeManager getScreenShakeManager(ServerLevel level) {
		return ((ScreenShakeManagerInterface)level).frozenLib$getScreenShakeManager();
	}

	public ScreenShakeStorage createData() {
		return new ScreenShakeStorage(this);
	}

	public ScreenShakeStorage createData(CompoundTag nbt) {
		return this.createData().load(nbt);
	}

	public static void addScreenShake(Level level, float intensity, double x, double y, double z, float maxDistance) {
		addScreenShake(level, intensity, 20, 5, x, y, z, maxDistance);
	}

	public static void addScreenShake(Level level, float intensity, int duration, double x, double y, double z, float maxDistance) {
		addScreenShake(level, intensity, duration, 1, x, y, z, maxDistance);
	}

	public static void addScreenShake(Level level, float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance) {
		addScreenShake(level, intensity, duration, falloffStart, x, y, z, maxDistance, 0);
	}

	public static void addScreenShake(Level level, float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance, int ticks) {
		if (!level.isClientSide) {
			ServerLevel serverLevel = (ServerLevel) level;
			ScreenShakeManager.getScreenShakeManager(serverLevel).addShake(intensity, duration, falloffStart, new Vec3(x, y, z), maxDistance, ticks);
		}
	}

	public static void sendScreenShakePacketTo(ServerPlayer player, float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance, int ticks) {
		ServerPlayNetworking.send(player, FrozenMain.SCREEN_SHAKE_PACKET, createScreenShakeByteBuf(intensity, duration, falloffStart, x, y, z, maxDistance, ticks));
	}

	public static FriendlyByteBuf createScreenShakeByteBuf(float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance, int ticks) {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeFloat(intensity);
		byteBuf.writeInt(duration);
		byteBuf.writeInt(falloffStart);
		byteBuf.writeDouble(x);
		byteBuf.writeDouble(y);
		byteBuf.writeDouble(z);
		byteBuf.writeFloat(maxDistance);
		byteBuf.writeInt(ticks);
		return byteBuf;
	}

	//With Entity
	public static void addEntityScreenShake(Entity entity, float intensity, float maxDistance) {
		addEntityScreenShake(entity, intensity, 5, 1, maxDistance);
	}

	public static void addEntityScreenShake(Entity entity, float intensity, int duration, float maxDistance) {
		addEntityScreenShake(entity, intensity, duration, 1, maxDistance);
	}

	public static void addEntityScreenShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance) {
		addEntityScreenShake(entity, intensity, duration, falloffStart, maxDistance, 0);
	}

	public static void addEntityScreenShake(Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		if (!entity.level.isClientSide) {
			FriendlyByteBuf byteBuf = createEntityScreenShakeByteBuf(entity, intensity, duration, falloffStart, maxDistance, ticks);
			for (ServerPlayer player : PlayerLookup.world((ServerLevel) entity.level)) {
				ServerPlayNetworking.send(player, FrozenMain.SCREEN_SHAKE_ENTITY_PACKET, byteBuf);
			}
			((EntityScreenShakeInterface)entity).addScreenShake(intensity, duration, falloffStart, maxDistance, ticks);
		}
	}

	public static void sendEntityScreenShakeTo(ServerPlayer player, Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		ServerPlayNetworking.send(player, FrozenMain.SCREEN_SHAKE_ENTITY_PACKET, createEntityScreenShakeByteBuf(entity, intensity, duration, falloffStart, maxDistance, ticks));
	}

	public static FriendlyByteBuf createEntityScreenShakeByteBuf(Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeVarInt(entity.getId());
		byteBuf.writeFloat(intensity);
		byteBuf.writeInt(duration);
		byteBuf.writeInt(falloffStart);
		byteBuf.writeFloat(maxDistance);
		byteBuf.writeInt(ticks);
		return byteBuf;
	}

}
