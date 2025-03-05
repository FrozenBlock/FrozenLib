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

package net.frozenblock.lib.loot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import io.netty.util.internal.UnstableApi;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UnstableApi
public class MutableLootTable {
	private final ArrayList<MutableLootPool> pools;
	private final ArrayList<LootItemFunction> functions = new ArrayList<>();
	private final LootContextParamSet paramSet;
	private final ResourceLocation randomSequence;

	public MutableLootTable(@NotNull LootTable table) {
		pools = createLootPools(table.pools);
		functions.addAll(table.functions);
		paramSet = table.getParamSet();
		randomSequence = table.randomSequence.orElse(null);
	}

	public static @Nullable MutableLootTable getMutable(@NotNull ResourceKey<LootTable> lootTableKey, ResourceKey<LootTable> id, LootTable lootTable) {
		if (lootTableKey.equals(id)) {
			return new MutableLootTable(lootTable);
		} else return null;
	}

	public static @Nullable MutableLootTable getMutable(ResourceKey<LootTable> lootTableKey, ResourceKey<LootTable> id, LootTable lootTable, @NotNull LootTableSource source) {
		if (source.isBuiltin()) {
			return getMutable(lootTableKey, id, lootTable);
		} else return null;
	}

	public LootTable build() {
		LootTable.Builder builder = LootTable.lootTable();
		builder.setParamSet(paramSet);
		builder.setRandomSequence(randomSequence);
		pools.forEach(mPool -> builder.pools.add(mPool.build()));
		functions.forEach(builder.functions::add);
		return builder.build();
	}


	/**
	 * Runs the consumer on each pool.
	 *
	 * @return This.
	 */
	public MutableLootTable modifyPools(Consumer<MutableLootPool> consumer) {
		pools.forEach(consumer);
		return this;
	}

	/**
	 * Runs the consumer on each pool that matches the condition.
	 *
	 * @return This.
	 */
	public MutableLootTable modifyPools(Predicate<MutableLootPool> condition, Consumer<MutableLootPool> consumer) {
		pools.forEach(pool -> {
			if (condition.test(pool)) {
				consumer.accept(pool);
			}
		});
		return this;
	}

	/**
	 * Converts a list of loot pools to an array list of mutable loot pools
	 *
	 * @param lootPoolList A list of {@link LootPool}s to copy.
	 * @return An array list of converted loot pools from input.
	 */
	private static @NotNull ArrayList<MutableLootPool> createLootPools(@NotNull List<LootPool> lootPoolList) {
		ArrayList<MutableLootPool> lootPools = new ArrayList<>();
		lootPoolList.forEach(pool -> lootPools.add(new MutableLootPool(pool)));
		return lootPools;
	}

	/**
	 * Returns if a pool has the given item.
	 *
	 * @param item The {@link Item}s to check for.
	 * @return A predicate that checks if the pool has the given item.
	 */
	@Contract(pure = true)
	public static @NotNull Predicate<MutableLootPool> has(Item item) {
		return lootPool -> lootPool.hasItem(item);
	}

	/**
	 * Returns if a pool has any of the given items.
	 *
	 * @param items The {@link Item}s to check for.
	 * @return A predicate that checks if the pool has any of the given items.
	 */
	@Contract(pure = true)
	public static @NotNull Predicate<MutableLootPool> hasAny(Item... items) {
		return lootPool -> lootPool.hasAnyItems(items);
	}

	/**
	 * Returns if a pool has all the given items.
	 *
	 * @param items The {@link Item}s to check for.
	 * @return A predicate that checks if the pool has all the given items.
	 */
	@Contract(pure = true)
	public static @NotNull Predicate<MutableLootPool> hasAll(Item... items) {
		return lootPool -> lootPool.hasAllItems(items);
	}
}
