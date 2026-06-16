package net.frozenblock.lib.event.impl;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.frozenblock.lib.loot.api.FrozenLibLootTableEvents;

/**
 * Redirects Fabric's {@link LootTableEvents} into {@link FrozenLibLootTableEvents}'s cross-platform events.
 */
public class FabricLootTableEventBridge {

	public static void init() {
		LootTableEvents.REPLACE.register((key, original, source, registries) ->
			FrozenLibLootTableEvents.REPLACE.invoker().replaceLootTable(key, original, registries));

		LootTableEvents.MODIFY.register((key, builder, source, registries) ->
			FrozenLibLootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, registries));

		LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) ->
			FrozenLibLootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootRegistry));

		LootTableEvents.MODIFY_DROPS.register((holder, context, drops) ->
			FrozenLibLootTableEvents.MODIFY_DROPS.invoker().modifyLootTableDrops(holder, context, drops));
	}
}
