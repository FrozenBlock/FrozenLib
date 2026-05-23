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

package net.frozenblock.lib.debug.client.gui;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.levelgen.structure.impl.status.StructureStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DebugEntryStructureStatuses implements DebugScreenEntry {
	private static final Identifier GROUP = FrozenLibConstants.id("structure_status");

	@Override
	public void display(
		final DebugScreenDisplayer displayer,
		@Nullable final Level serverOrClientLevel,
		@Nullable final LevelChunk clientChunk,
		@Nullable final LevelChunk serverChunk
	) {
		final Minecraft minecraft = Minecraft.getInstance();
		final Entity entity = minecraft.getCameraEntity();
		if (entity == null) return;

		entity.getAttachedOrElse(StructureStatus.ATTACHMENT_TYPE, ImmutableList.of()).forEach(structureStatus -> {
			displayer.addToGroup(GROUP, "Structure: " + structureStatus.structure() + " Inside Piece: " + structureStatus.insidePiece());
		});
	}
}
