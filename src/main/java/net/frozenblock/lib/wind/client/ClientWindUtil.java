/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.wind.client;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

public class ClientWindUtil {

	@ApiStatus.Internal
	public static void init() {
		ClientTickEvents.START_LEVEL_TICK.register(Debug::tick);

		ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> WindManager.getOrCreate(level).trackOrUntrackDisturbanceTarget(entity));
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> WindManager.getOrCreate(level).untrackDisturbanceTarget(entity));

		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).trackOrUntrackDisturbanceTarget(blockEntity));
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> WindManager.getOrCreate(level).untrackDisturbanceTarget(blockEntity));

		ClientChunkEvents.CHUNK_LOAD.register((clientLevel, chunk) -> WindManager.getOrCreate(clientLevel).trackOrUntrackDisturbanceTarget(chunk));
		ClientChunkEvents.CHUNK_UNLOAD.register((clientLevel, chunk) -> WindManager.getOrCreate(clientLevel).untrackDisturbanceTarget(chunk));
	}

	@VisibleForDebug
	public static class Debug {
		private static final List<Vec3> ACCESSED_POSITIONS = new ArrayList<>();
		private static final List<Pair<AttachmentTarget, WindDisturbances>> WIND_DISTURBANCES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_NODES = new ArrayList<>();
		private static final List<List<Pair<Vec3, Integer>>> DEBUG_DISTURBANCE_NODES = new ArrayList<>();

		public static void tick(ClientLevel level) {
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();

			if (FrozenLibConstants.DEBUG_WIND) DEBUG_NODES.addAll(createWindNodes(level));
			if (FrozenLibConstants.DEBUG_WIND_DISTURBANCES) {
				WIND_DISTURBANCES.addAll(WindManager.getOrCreate(level).getWindDisturbances());
				DEBUG_DISTURBANCE_NODES.addAll(createWindDisturbanceNodes(level));
			}

			ACCESSED_POSITIONS.clear();
		}

		@VisibleForDebug
		public static void addAccessedPosition(Vec3 vec3) {
			ACCESSED_POSITIONS.add(vec3);
		}

		@VisibleForDebug
		public static void clear() {
			ACCESSED_POSITIONS.clear();
			WIND_DISTURBANCES.clear();
			DEBUG_NODES.clear();
			DEBUG_DISTURBANCE_NODES.clear();
		}

		@VisibleForDebug
		public static List<List<Pair<Vec3, Integer>>> getDebugNodes() {
			return DEBUG_NODES;
		}

		private static List<List<Pair<Vec3, Integer>>> createWindNodes(ClientLevel level) {
			final List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			ImmutableList.copyOf(ACCESSED_POSITIONS).forEach(vec3 -> windNodes.add(createWindNodes(level, vec3, 1.5D, false)));
			return windNodes;
		}

		@VisibleForDebug
		@Unmodifiable
		public static List<Pair<AttachmentTarget, WindDisturbances>> getWindDisturbances() {
			return ImmutableList.copyOf(WIND_DISTURBANCES);
		}

		@VisibleForDebug
		public static List<List<Pair<Vec3, Integer>>> getDebugDisturbanceNodes() {
			return DEBUG_DISTURBANCE_NODES;
		}

		private static List<List<Pair<Vec3, Integer>>> createWindDisturbanceNodes(ClientLevel level) {
			final List<List<Pair<Vec3, Integer>>> windNodes = new ArrayList<>();
			WIND_DISTURBANCES.forEach(
				tracked -> {
					final AttachmentTarget target = tracked.getFirst();
					for (WindDisturbance disturbance : tracked.getSecond()) {
						final AABB area = disturbance.area(target, level, disturbance.origin(target, level));
						BlockPos.betweenClosed(
							BlockPos.containing(area.getMinPosition()),
							BlockPos.containing(area.getMaxPosition())
						).forEach(
							blockPos -> {
								final Vec3 blockPosCenter = Vec3.atCenterOf(blockPos);
								windNodes.add(createWindNodes(level, blockPosCenter, 1D, true));
							}
						);
					}
				}
			);
			return windNodes;
		}

		private static List<Pair<Vec3, Integer>> createWindNodes(Level level, Vec3 origin, double stretch, boolean disturbanceOnly) {
			final List<Pair<Vec3, Integer>> windNodes = new ArrayList<>();
			final WindManager windManager = WindManager.getOrCreate(level);
			Vec3 wind = disturbanceOnly ?
				windManager.getRawDisturbanceMovement(origin)
				: windManager.getWindMovement(origin);

			final double windLength = wind.length();
			if (windLength == 0D) return windNodes;

			final int increments = 3;
			Vec3 lineStart = origin;
			double windLineScale = (1D / increments) * stretch;
			windNodes.add(
				Pair.of(
					lineStart,
					calculateNodeColor(Math.min(1D, windLength), disturbanceOnly)
				)
			);

			for (int i = 0; i < increments; ++i) {
				final Vec3 lineEnd = lineStart.add(wind.scale(windLineScale));
				windNodes.add(
					Pair.of(
						lineEnd,
						calculateNodeColor(Math.min(1D, windLength), disturbanceOnly)
					)
				);
				lineStart = lineEnd;
				wind = disturbanceOnly ?
					windManager.getRawDisturbanceMovement(lineStart)
					: windManager.getWindMovement(lineStart);
			}

			return windNodes;
		}

		private static int calculateNodeColor(double strength, boolean disturbanceOnly) {
			return ARGB.color(
				255,
				(int) Mth.lerp(strength, 255, 0),
				(int) Mth.lerp(strength, 90, 255),
				disturbanceOnly ? 0 : 255
			);
		}
	}
}
