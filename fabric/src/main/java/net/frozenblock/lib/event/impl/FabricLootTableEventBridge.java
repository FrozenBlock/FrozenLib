/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.event.impl;

// TODO: wait for fabric to restore the loot api
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableEvents;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableSource;

/**
 * Redirects Fabric's {@link LootTableEvents} into {@link FrozenLibLootTableEvents}'s cross-platform events.
 */
public class FabricLootTableEventBridge {

	public static void init() {
		LootTableEvents.REPLACE.register((key, original, source, registries) ->
			FrozenLibLootTableEvents.REPLACE.invoker().replaceLootTable(key, original, toFrozenLibSource(source), registries));

		LootTableEvents.MODIFY.register((key, builder, source, registries) ->
			FrozenLibLootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, toFrozenLibSource(source), registries));

		LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) ->
			FrozenLibLootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootRegistry));

		LootTableEvents.MODIFY_DROPS.register((holder, context, drops) ->
			FrozenLibLootTableEvents.MODIFY_DROPS.invoker().modifyLootTableDrops(holder, context, drops));
	}

	private static FrozenLibLootTableSource toFrozenLibSource(LootTableSource source) {
		return switch (source) {
			case VANILLA -> FrozenLibLootTableSource.VANILLA;
			case MOD -> FrozenLibLootTableSource.MOD;
			case DATA_PACK -> FrozenLibLootTableSource.DATA_PACK;
			case REPLACED -> FrozenLibLootTableSource.REPLACED;
		};
	}
}
