/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.worldgen.vein.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.levelgen.OreVeinifier;

public class FrozenVeinTypes {
	private static final Map<String, OreVeinifier.VeinType> NEW_VEIN_TYPES = new LinkedHashMap<>();

	public static void addVeinType(String id, OreVeinifier.VeinType veinType) {
		NEW_VEIN_TYPES.put(id, veinType);
	}

	public static OreVeinifier.VeinType getVeinType(String modId, String name) {
		return NEW_VEIN_TYPES.get(modId.toUpperCase() + name.toUpperCase());
	}
}
