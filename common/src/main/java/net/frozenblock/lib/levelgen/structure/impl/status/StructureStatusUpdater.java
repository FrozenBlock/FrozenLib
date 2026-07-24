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

package net.frozenblock.lib.levelgen.structure.impl.status;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.TickEvents;
import net.frozenblock.lib.networking.api.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public final class StructureStatusUpdater {

	public static void init() {
		TickEvents.START_LEVEL_TICK.register(StructureStatusUpdater::updatePlayerStructureStatusesForLevel);
	}

	public static void updatePlayerStructureStatusesForLevel(ServerLevel level) {
		PlayerLookup.level(level).forEach(player -> {
			if (!player.connection.hasClientLoaded()) return;
			updatePlayerStructureStatus(level.registryAccess().lookupOrThrow(Registries.STRUCTURE), level.structureManager(), player);
		});
	}

	private static void updatePlayerStructureStatus(Registry<Structure> structureRegistry, StructureManager structureManager, ServerPlayer player) {
		final BlockPos pos = player.blockPosition();
		final List<StructureStatus> newStructureStatuses = new ArrayList<>();

		for (Structure structure : structureManager.getAllStructuresAt(pos).keySet()) {
			final StructureStart structureStart = structureManager.getStructureAt(pos, structure);
			if (structureStart == StructureStart.INVALID_START) continue;

			final boolean insidePiece = structureManager.structureHasPieceAt(pos, structureStart);
			newStructureStatuses.add(new StructureStatus(structureRegistry.getKey(structure), insidePiece));
		}

		if (!newStructureStatuses.equals(StructureStatus.ATTACHMENT_TYPE.get(player))) {
			StructureStatus.ATTACHMENT_TYPE.set(player, newStructureStatuses);
		}
	}
}
