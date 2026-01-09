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

package net.frozenblock.lib.worldgen.structure.mixin.status;

import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatus;
import net.frozenblock.lib.worldgen.structure.impl.status.PlayerStructureStatusInterface;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements PlayerStructureStatusInterface {

	@Unique
	@Nullable
	private List<PlayerStructureStatus> frozenLib$structureStatuses;

	@Unique
	@Override
	public List<PlayerStructureStatus> frozenLib$getStructureStatuses() {
		if (this.frozenLib$structureStatuses == null) return List.of();
		return this.frozenLib$structureStatuses;
	}

	@Unique
	@Override
	public void frozenLib$setStructureStatuses(List<PlayerStructureStatus> statuses) {
		this.frozenLib$structureStatuses = statuses;
	}
}
