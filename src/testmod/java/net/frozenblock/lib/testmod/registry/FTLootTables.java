package net.frozenblock.lib.testmod.registry;

import net.frozenblock.lib.loot.LootTableModifier;
import net.frozenblock.lib.loot.MutableLootTable;
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
		LootTableModifier.editTable(
			BuiltInLootTables.SPAWN_BONUS_CHEST, false,
			(id, mutableLootTable) -> mutableLootTable.modifyPools(
				MutableLootTable.has(Items.ACACIA_LOG),
				(lootPool) -> lootPool.add(Items.DIAMOND_BLOCK, 3, SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
			)
		);

		//Cold Ocean Ruin Archaeology
		LootTableModifier.editTable(
			BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, false,
			(id, mutableLootTable) -> mutableLootTable.modifyPools(
				MutableLootTable.has(Items.MOURNER_POTTERY_SHERD),
				(lootPool) -> lootPool.replace(Items.MOURNER_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD)
			)
		);

	}
}
