/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class StructureStatusUpdater {

	public static void updatePlayerStructureStatusesForLevel(ServerLevel level) {
		StructureManager structureManager = level.structureManager();
		level.players().forEach(player -> updatePlayerStructureStatus(structureManager, player));
	}

	private static void updatePlayerStructureStatus(StructureManager structureManager, ServerPlayer player) {
		if (!(player instanceof PlayerStructureStatusInterface structureStatusInterface)) return;

		final BlockPos pos = player.blockPosition();
		final List<PlayerStructureStatus> newStructureStatuses = new ArrayList<>();
		final List<PlayerStructureStatus> currentStructureStatuses = structureStatusInterface.frozenLib$getStructureStatuses();

		for (Structure structure : structureManager.getAllStructuresAt(pos).keySet()) {
			final StructureStart structureStart = structureManager.getStructureAt(pos, structure);
			if (structureStart == StructureStart.INVALID_START) continue;

			if (!((Object) structureStart instanceof StructureStartInterface structureStartInterface)) continue;

			final Identifier structureLocation = structureStartInterface.frozenLib$getId();
			if (structureLocation != null) {
				boolean insidePiece = structureManager.structureHasPieceAt(pos, structureStart);
				boolean addNewStructureStatus = true;
				for (PlayerStructureStatus existingStatus : newStructureStatuses) {
					if (!existingStatus.getStructure().equals(structureLocation)) continue;
					addNewStructureStatus = false;
					if (!existingStatus.isInsidePiece() && insidePiece) existingStatus.setInsidePiece(true);
				}
				if (addNewStructureStatus) newStructureStatuses.add(new PlayerStructureStatus(structureLocation, insidePiece));
			} else if (FrozenLibConstants.UNSTABLE_LOGGING) {
				throw new AssertionError("Structure piece doesn't contain an id!");
			}
		}

		if (!newStructureStatuses.equals(currentStructureStatuses)) {
			structureStatusInterface.frozenLib$setStructureStatuses(newStructureStatuses);
			sendStructureStatusPacket(player, newStructureStatuses);
		}
	}

	private static void sendStructureStatusPacket(ServerPlayer player, List<PlayerStructureStatus> statuses) {
		player.connection.send(new ClientboundCustomPayloadPacket(new PlayerStructureStatusPacket(statuses)));
	}
}
