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

package net.frozenblock.lib.worldgen.structure.impl.status;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.structure.impl.StructureStartInterface;
import net.frozenblock.lib.worldgen.structure.impl.status.networking.PlayerStructureStatusPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class StructureStatusUpdater {

	@ApiStatus.Internal
	public static void updatePlayerStructureStatusesForLevel(@NotNull ServerLevel level) {
		StructureManager structureManager = level.structureManager();
		level.players().forEach(player -> updatePlayerStructureStatus(structureManager, player));
	}

	@ApiStatus.Internal
	private static void updatePlayerStructureStatus(@NotNull StructureManager structureManager, @NotNull ServerPlayer player) {
		if (player instanceof PlayerStructureStatusInterface structureStatusInterface) {
			BlockPos pos = player.blockPosition();

			List<PlayerStructureStatus> newStructureStatuses = new ArrayList<>();
			List<PlayerStructureStatus> currentStructureStatuses = structureStatusInterface.frozenLib$getStructureStatuses();

			for (Structure structure : structureManager.getAllStructuresAt(pos).keySet()) {
				StructureStart structureStart = structureManager.getStructureAt(pos, structure);
				if (structureStart != StructureStart.INVALID_START) {
					if ((Object) structureStart instanceof StructureStartInterface structureStartInterface) {
						ResourceLocation structureLocation = structureStartInterface.frozenLib$getId();
						if (structureLocation != null) {
							boolean insidePiece = structureManager.structureHasPieceAt(pos, structureStart);
							boolean addNewStructureStatus = true;
							for (PlayerStructureStatus existingStatus : newStructureStatuses) {
								if (existingStatus.getStructure().equals(structureLocation)) {
									addNewStructureStatus = false;
									if (!existingStatus.isInsidePiece() && insidePiece) existingStatus.setInsidePiece(true);
								}
							}
							if (addNewStructureStatus) newStructureStatuses.add(new PlayerStructureStatus(structureLocation, insidePiece));
						} else if (FrozenLibConstants.UNSTABLE_LOGGING) {
							throw new AssertionError("Structure piece doesn't contain an id!");
						}
					}
				}
			}

			if (!newStructureStatuses.equals(currentStructureStatuses)) {
				structureStatusInterface.frozenLib$setStructureStatuses(newStructureStatuses);
				sendStructureStatusPacket(player, newStructureStatuses);
			}
		}
	}

	@ApiStatus.Internal
	private static void sendStructureStatusPacket(@NotNull ServerPlayer player, @NotNull List<PlayerStructureStatus> structureStatuses) {
		player.connection.send(new ClientboundCustomPayloadPacket(new PlayerStructureStatusPacket(structureStatuses)));
	}
}
