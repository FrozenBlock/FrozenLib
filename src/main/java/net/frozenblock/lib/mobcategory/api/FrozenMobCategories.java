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

package net.frozenblock.lib.mobcategory.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.entity.MobCategory;

public final class FrozenMobCategories {
	private FrozenMobCategories() {
		throw new UnsupportedOperationException("FrozenMobCategories contains only static declarations.");
	}

	private static final Map<String, MobCategory> NEW_MOB_CATEROGIES = new LinkedHashMap<>();

	public static void addMobCategory(String id, MobCategory category) {
		NEW_MOB_CATEROGIES.put(id, category);
	}

	public static MobCategory getCategory(String modId, String name) {
		return NEW_MOB_CATEROGIES.get(modId.toUpperCase() + name.toUpperCase());
	}
}
