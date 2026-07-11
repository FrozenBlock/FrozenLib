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

import net.frozenblock.lib.FrozenLibConstants;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ClientOnly
public final class FrozenLibDebugScreenEntries {
	public static final Identifier STRUCTURE_STATUSES = register("structure_status", new DebugEntryStructureStatuses());

	public static void init() {}

	private static Identifier register(String name, DebugScreenEntry entry) {
		return DebugScreenEntries.register(FrozenLibConstants.id(name), entry);
	}
}
