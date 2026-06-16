package net.frozenblock.lib.event.impl;

import net.frozenblock.lib.loot.api.FrozenLibLootTableEvents;
import net.frozenblock.lib.loot.mixin.neoforge.LootTableAccessor;
import net.frozenblock.lib.loot.mixin.neoforge.LootTableBuilderAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/**
 * Drives {@link FrozenLibLootTableEvents#REPLACE} and {@link FrozenLibLootTableEvents#MODIFY} from NeoForge's
 * {@link LootTableLoadEvent}, the only native loot table event NeoForge provides.
 */
public class NeoLootTableEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(LootTableLoadEvent.class, NeoLootTableEventBridge::onLootTableLoad);
	}

	private static void onLootTableLoad(LootTableLoadEvent event) {
		ResourceKey<LootTable> key = event.getKey();
		LootTable original = event.getTable();
		HolderLookup.Provider registries = event.getRegistries();

		LootTable replaced = FrozenLibLootTableEvents.REPLACE.invoker().replaceLootTable(key, original, registries);
		if (replaced != null) {
			event.setTable(replaced);
			return;
		}

		LootTable.Builder builder = toBuilder(original);

		FrozenLibLootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, registries);
		event.setTable(builder.build());
	}

	public static LootTable.Builder toBuilder(LootTable lootTable) {
		LootTableAccessor accessor = (LootTableAccessor) lootTable;
		LootTable.Builder builder = LootTable.lootTable();
		builder.setParamSet(lootTable.getParamSet());
		builder.setRandomSequence(accessor.frozenLib$getRandomSequence().orElse(null));
		((LootTableBuilderAccessor) builder).frozenLib$getPools().addAll(accessor.frozenLib$getPools());
		((LootTableBuilderAccessor) builder).frozenLib$getFunctions().addAll(accessor.frozenLib$getFunctions());
		return builder;
	}
}
