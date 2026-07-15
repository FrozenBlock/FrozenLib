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

import net.frozenblock.lib.item.api.loot.LootTableEvents;
import net.frozenblock.lib.item.api.loot.LootTableSource;

/**
 * Redirects Fabric's {@link net.fabricmc.fabric.api.loot.v3.LootTableEvents} into {@link LootTableEvents}'s cross-platform events.
 */
public class FabricLootTableEventBridge {

	public static void init() {
		net.fabricmc.fabric.api.loot.v3.LootTableEvents.REPLACE.register((key, original, source, registries) ->
			LootTableEvents.REPLACE.invoker().replaceLootTable(key, original, toFrozenLibSource(source), registries));

		net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register((key, builder, source, registries) ->
			LootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, toFrozenLibSource(source), registries));

		net.fabricmc.fabric.api.loot.v3.LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) ->
			LootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootRegistry));

		net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY_DROPS.register((holder, context, drops) ->
			LootTableEvents.MODIFY_DROPS.invoker().modifyLootTableDrops(holder, context, drops));
	}

	private static LootTableSource toFrozenLibSource(net.fabricmc.fabric.api.loot.v3.LootTableSource source) {
		return switch (source) {
			case VANILLA -> LootTableSource.VANILLA;
			case MOD -> LootTableSource.MOD;
			case DATA_PACK -> LootTableSource.DATA_PACK;
			case REPLACED -> LootTableSource.REPLACED;
		};
	}
}
