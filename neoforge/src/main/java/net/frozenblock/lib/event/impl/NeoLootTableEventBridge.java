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
import net.frozenblock.lib.loot.impl.NeoLootUtil;
import net.frozenblock.lib.loot.mixin.neoforge.LootTableAccessor;
import net.frozenblock.lib.loot.mixin.neoforge.LootTableBuilderAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/**
 * Drives {@link LootTableEvents#REPLACE} and {@link LootTableEvents#MODIFY} from NeoForge's
 * {@link LootTableLoadEvent}, the only native loot table event NeoForge provides.
 */
public class NeoLootTableEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(LootTableLoadEvent.class, NeoLootTableEventBridge::onLootTableLoad);
	}

	private static void onLootTableLoad(LootTableLoadEvent event) {
		final ResourceKey<LootTable> key = event.getKey();
		final LootTable original = event.getTable();
		final HolderLookup.Provider registries = event.getRegistries();

		LootTableSource source = NeoLootUtil.SOURCES.get().getOrDefault(key.identifier(), LootTableSource.DATA_PACK);

		final LootTable replaced = LootTableEvents.REPLACE.invoker().replaceLootTable(key, original, source, registries);
		LootTable table = original;
		if (replaced != null) {
			table = replaced;
			source = LootTableSource.REPLACED;
		}

		final LootTable.Builder builder = toBuilder(table);

		LootTableEvents.MODIFY.invoker().modifyLootTable(key, builder, source, registries);

		final LootTable built = builder.build();
		built.setLootTableId(key.identifier());
		event.setTable(built);
	}

	public static LootTable.Builder toBuilder(LootTable original) {
		final LootTableAccessor accessor = (LootTableAccessor) original;
		final LootTable.Builder builder = LootTable.lootTable();

		builder.setParamSet(original.getParamSet());
		accessor.frozenLib$getRandomSequence().ifPresent(builder::setRandomSequence);
		((LootTableBuilderAccessor) builder).frozenLib$getPools().addAll(accessor.frozenLib$getPools());
		((LootTableBuilderAccessor) builder).frozenLib$getFunctions().addAll(accessor.frozenLib$getFunctions());
		return builder;
	}
}
