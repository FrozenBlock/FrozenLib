/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableModifier {

	public static void editTable(ResourceKey<LootTable> targetLootTable, boolean requiresBuiltIn, Edit listener) {
		LootTableEvents.Replace modification = (id, lootTable, source, registries) -> {
			if ((requiresBuiltIn && !source.isBuiltin()) || !targetLootTable.equals(id)) return null;
			MutableLootTable mutableLootTable = new MutableLootTable(lootTable);
			listener.editLootTable(id, mutableLootTable);
			return mutableLootTable.build();
		};

		LootTableEvents.REPLACE.register(modification);
	}

	@FunctionalInterface
	public interface Edit {
		/**
		 * Edits loot tables.
		 *
		 * @param id The loot table key.
		 * @param mutableLootTable The mutable copy of the loot table.
		 */
		void editLootTable(ResourceKey<LootTable> id, MutableLootTable mutableLootTable);
	}
}
