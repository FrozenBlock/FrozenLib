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
