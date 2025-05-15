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

package net.frozenblock.lib.screenshake.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.ScreenShakeManagerInterface;
import net.frozenblock.lib.screenshake.impl.network.EntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.ScreenShakePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScreenShakeManager extends SavedData {
	public static final String SCREENSHAKE_MANAGER_FILE_ID = "frozenlib_screen_shakes";
	public static final Codec<ScreenShakeManager> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			ScreenShake.LIST_CODEC.fieldOf("ScreenShakes").forGetter(screenshakeManager -> screenshakeManager.shakes)
			).apply(instance, ScreenShakeManager::createFromCodec)
	);
	public static final SavedDataType<ScreenShakeManager> TYPE = new SavedDataType<>(SCREENSHAKE_MANAGER_FILE_ID, ScreenShakeManager::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);

	private final ArrayList<ScreenShake> shakes = new ArrayList<>();

	public ScreenShakeManager() {
	}

	public static @NotNull ScreenShakeManager createFromCodec(List<ScreenShake> shakes) {
		ScreenShakeManager screenShakeManager = new ScreenShakeManager();
		screenShakeManager.shakes.addAll(shakes);
		return screenShakeManager;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public void tick(@NotNull ServerLevel level) {
		if (level.tickRateManager().runsNormally()) {
			this.getShakes().removeIf(ScreenShake::shouldRemove);
			for (ScreenShake shake : this.getShakes()) {
				if (level.getChunkSource().hasChunk(shake.chunkPos.x, shake.chunkPos.z)) {
					shake.ticks += 1;
					Collection<ServerPlayer> playersTrackingChunk = PlayerLookup.tracking(level, shake.chunkPos);
					for (ServerPlayer serverPlayer : playersTrackingChunk) {
						if (!shake.trackingPlayers.contains(serverPlayer)) {
							ScreenShakeManager.sendScreenShakePacketTo(serverPlayer, shake.getIntensity(), shake.getDuration(), shake.getDurationFalloffStart(), shake.getPos(), shake.getMaxDistance(), shake.getTicks());
						}
					}
					shake.trackingPlayers.clear();
					shake.trackingPlayers.addAll(playersTrackingChunk);
				}
			}
		}
	}

	public void addShake(float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		this.getShakes().add(new ScreenShake(intensity, duration, falloffStart, pos, maxDistance, ticks));
	}

	public static ScreenShakeManager getOrCreateScreenShakeManager(ServerLevel level) {
		return ((ScreenShakeManagerInterface)level).frozenLib$getOrCreateScreenShakeManager();
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

	public static void addScreenShake(@NotNull Level level, float intensity, int duration, int falloffStart, double x, double y, double z, float maxDistance, int ticks) {
		if (level instanceof ServerLevel serverLevel) {
			ScreenShakeManager.getOrCreateScreenShakeManager(serverLevel).addShake(intensity, duration, falloffStart, new Vec3(x, y, z), maxDistance, ticks);
		}
	}

	public static void sendScreenShakePacketTo(ServerPlayer player, float intensity, int duration, int falloffStart, Vec3 pos, float maxDistance, int ticks) {
		ServerPlayNetworking.send(player, new ScreenShakePacket(intensity, duration, falloffStart, pos, maxDistance, ticks));
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

	public static void addEntityScreenShake(@NotNull Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		if (entity.level() instanceof ServerLevel serverLevel) {
			EntityScreenShakePacket packet = new EntityScreenShakePacket(entity.getId(), intensity, duration, falloffStart, maxDistance, ticks);
			for (ServerPlayer player : PlayerLookup.world(serverLevel)) {
				ServerPlayNetworking.send(player, packet);
			}
			((EntityScreenShakeInterface) entity).frozenLib$addScreenShake(intensity, duration, falloffStart, maxDistance, ticks);
		}
	}

	public static void sendEntityScreenShakeTo(ServerPlayer player, @NotNull Entity entity, float intensity, int duration, int falloffStart, float maxDistance, int ticks) {
		ServerPlayNetworking.send(player, new EntityScreenShakePacket(entity.getId(), intensity, duration, falloffStart, maxDistance, ticks));
	}

	public static class ScreenShake {
		public static final Codec<ScreenShake> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.FLOAT.fieldOf("Intensity").forGetter(ScreenShake::getIntensity),
			Codec.INT.fieldOf("Duration").forGetter(ScreenShake::getDuration),
			Codec.INT.fieldOf("FalloffStart").forGetter(ScreenShake::getDurationFalloffStart),
			Vec3.CODEC.fieldOf("Position").forGetter(ScreenShake::getPos),
			Codec.FLOAT.fieldOf("MaxDistance").forGetter(ScreenShake::getMaxDistance),
			Codec.INT.fieldOf("Ticks").forGetter(ScreenShake::getTicks)
		).apply(instance, ScreenShake::new));
		public static final Codec<List<ScreenShake>> LIST_CODEC = CODEC.listOf();
		@Getter
		public final int duration;
		@Getter
		public final float maxDistance;
		public final ChunkPos chunkPos;
		@Getter
		private final float intensity;
		@Getter
		private final int durationFalloffStart;
		@Getter
		public int ticks;
		public List<ServerPlayer> trackingPlayers = new ArrayList<>();
		@Getter
		protected Vec3 pos;

		public ScreenShake(float intensity, int duration, int durationFalloffStart, Vec3 pos, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.pos = pos;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
			this.chunkPos = new ChunkPos(BlockPos.containing(pos));
		}

		public boolean shouldRemove() {
			return this.ticks > this.duration;
		}

	}
}
