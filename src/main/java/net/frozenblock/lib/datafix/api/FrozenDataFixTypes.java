/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.datafix.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.NotNull;

public final class FrozenDataFixTypes {
	private FrozenDataFixTypes() {
		throw new UnsupportedOperationException("FrozenDataFixTypes contains only static declarations.");
	}

	private static final Map<String, DataFixTypes> NEW_DATA_FIX_TYPES = new LinkedHashMap<>();

	public static void addDataFixType(String id, DataFixTypes dataFixTypes) {
		NEW_DATA_FIX_TYPES.put(id, dataFixTypes);
	}

	public static DataFixTypes getDataFixType(@NotNull String modId, @NotNull String name) {
		return NEW_DATA_FIX_TYPES.get(modId.toUpperCase() + name.toUpperCase());
	}
}
