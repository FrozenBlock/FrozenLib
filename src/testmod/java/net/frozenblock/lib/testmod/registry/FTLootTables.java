/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.testmod.registry;

import net.frozenblock.lib.loot.api.LootTableModificationApi;
import net.frozenblock.lib.loot.impl.MutableLootTable;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class FTLootTables {
	private FTLootTables() {
		throw new UnsupportedOperationException("FTLootTables only supports static declarations.");
	}

	public static void init() {
		FrozenTestMain.LOGGER.info("Registering Loot Table Modifications for FrozenTest.");
		//BONUS CHEST
		LootTableModificationApi.editTable(
			BuiltInLootTables.SPAWN_BONUS_CHEST, false,
			(id, mutableLootTable) -> mutableLootTable.modifyPools(
				MutableLootTable.has(Items.ACACIA_LOG),
				(lootPool) -> lootPool.add(Items.DIAMOND_BLOCK, 3, SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
			)
		);

		//Cold Ocean Ruin Archaeology
		LootTableModificationApi.editTable(
			BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, false,
			(id, mutableLootTable) -> mutableLootTable.modifyPools(
				MutableLootTable.has(Items.MOURNER_POTTERY_SHERD),
				(lootPool) -> lootPool.replace(Items.MOURNER_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD)
			)
		);

	}
}
