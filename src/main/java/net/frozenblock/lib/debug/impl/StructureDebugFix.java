/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.debug.impl;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StructureDebugFix {

	public static void init() {
		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			if (FrozenLibConfig.IS_DEBUG) {
				serverLevel.players().forEach(
					player -> {
						player.getChunkTrackingView().forEach(
							chunkPos -> {
								if (serverLevel.hasChunk(chunkPos.x, chunkPos.z)) {
									LevelChunk chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
									chunk.getAllStarts().values().forEach(
										structureStart -> {
											FrozenNetworking.sendPacketToAllPlayers(serverLevel, createStructurePayload(serverLevel, structureStart));
										}
									);
								}
							}
						);
					}
				);
			}
		});
	}

	@Contract("_, _ -> new")
	private static @NotNull StructuresDebugPayload createStructurePayload(ServerLevel serverLevel, @NotNull StructureStart structureStart) {
		List<StructuresDebugPayload.PieceInfo> pieces = new ArrayList<>();

		for (int i = 0; i < structureStart.getPieces().size(); ++i) {
			pieces.add(new StructuresDebugPayload.PieceInfo(structureStart.getPieces().get(i).getBoundingBox(), i == 0));
		}

		return new StructuresDebugPayload(serverLevel.dimension(), structureStart.getBoundingBox(), pieces);
	}

}
