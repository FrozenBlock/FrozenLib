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

package net.frozenblock.lib.entity.api.category;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.MobCategory;

@UtilityClass
public class FrozenMobCategories {
	private static final Map<String, MobCategory> NEW_MOB_CATEGORIES = new LinkedHashMap<>();

	public static void addMobCategory(String id, MobCategory category) {
		NEW_MOB_CATEGORIES.put(id, category);
	}

	public static MobCategory getCategory(String modId, String name) {
		return NEW_MOB_CATEGORIES.get(modId.toUpperCase() + name.toUpperCase());
	}
}
