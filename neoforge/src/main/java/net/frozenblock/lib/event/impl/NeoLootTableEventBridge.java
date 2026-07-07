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

import net.frozenblock.lib.item.api.loot.FrozenLibLootTableEvents;
import net.frozenblock.lib.item.api.loot.FrozenLibLootTableSource;
import net.frozenblock.lib.loot.impl.NeoLootUtil;
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

		FrozenLibLootTableSource source = NeoLootUtil.SOURCES.get().getOrDefault(key.identifier(), FrozenLibLootTableSource.DATA_PACK);

		LootTable replaced = FrozenLibLootTableEvents.REPLACE.invoker().replaceLootTable(key, original, source, registries);
		LootTable table = original;
		if (replaced != null) {
			table = replaced;
			source = FrozenLibLootTableSource.REPLACED;
		}

		LootTable.Builder builder = toBuilder(table);

		FrozenLibLootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, source, registries);
		event.setTable(builder.build());
	}

	public static LootTable.Builder toBuilder(LootTable lootTable) {
		LootTableAccessor accessor = (LootTableAccessor) lootTable;
		LootTable.Builder builder = LootTable.lootTable();
		builder.setParamSet(lootTable.getParamSet());
		accessor.frozenLib$getRandomSequence().ifPresent(builder::setRandomSequence);
		((LootTableBuilderAccessor) builder).frozenLib$getPools().addAll(accessor.frozenLib$getPools());
		((LootTableBuilderAccessor) builder).frozenLib$getFunctions().addAll(accessor.frozenLib$getFunctions());
		return builder;
	}
}
