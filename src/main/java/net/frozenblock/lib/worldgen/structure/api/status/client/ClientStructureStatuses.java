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

package net.frozenblock.lib.worldgen.structure.api.status.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
@Environment(EnvType.CLIENT)
public class ClientStructureStatuses {
	private static final List<PlayerStructureStatus> STRUCTURE_STATUSES = new ArrayList<>();

	public static Optional<PlayerStructureStatus> getProminentStructureStatus() {
		PlayerStructureStatus chosenStatus = null;
		for (PlayerStructureStatus structureStatus : STRUCTURE_STATUSES) {
			if (chosenStatus != null && !(!chosenStatus.isInsidePiece() && structureStatus.isInsidePiece())) continue;
			chosenStatus = structureStatus;
		}
		return Optional.ofNullable(chosenStatus);
	}

	@ApiStatus.Internal
	public static void setStructureStatuses(List<PlayerStructureStatus> structureStatuses) {
		clearStructureStatuses();
		STRUCTURE_STATUSES.addAll(structureStatuses);
	}

	@ApiStatus.Internal
	public static void clearStructureStatuses() {
		STRUCTURE_STATUSES.clear();
	}
}
