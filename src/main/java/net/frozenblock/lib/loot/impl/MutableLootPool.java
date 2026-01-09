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

package net.frozenblock.lib.loot.impl;

import io.netty.util.internal.UnstableApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

@UnstableApi
public class MutableLootPool {
	public ArrayList<LootPoolEntryContainer> entries = new ArrayList<>();
	public ArrayList<LootItemCondition> conditions = new ArrayList<>();
	public ArrayList<LootItemFunction> functions = new ArrayList<>();
	public NumberProvider rolls;
	public NumberProvider bonusRolls;

	public MutableLootPool(LootPool lootPool) {
		this.entries.addAll(lootPool.entries);
		this.conditions.addAll(lootPool.conditions);
		this.functions.addAll(lootPool.functions);
		this.rolls = lootPool.rolls;
		this.bonusRolls = lootPool.bonusRolls;
	}

	public LootPool build() {
		final LootPool.Builder builder = LootPool.lootPool();
		this.entries.forEach(builder.entries::add);
		this.conditions.forEach(builder.conditions::add);
		this.functions.forEach(builder.functions::add);
		builder.setRolls(rolls);
		builder.setBonusRolls(bonusRolls);
		return builder.build();
	}

	/**
	 * Adds one item to the loot pool
	 *
	 * @param item    item to add
	 * @param weight  how likely the item is to get drawn from the pool
	 * @param builder idk lol
	 * @return this
	 */
	public MutableLootPool add(ItemLike item, int weight, LootItemFunction.Builder builder) {
		this.entries.add(LootItem.lootTableItem(item).setWeight(weight).apply(builder).build());
		return this;
	}

	/**
	 * Adds one or more items to the loot pool with the same weight
	 *
	 * @param items   items to add
	 * @param weight  how likely the items are to get drawn from the pool
	 * @param builder idk lol
	 * @return this
	 */
	public MutableLootPool addAll(int weight, LootItemFunction.Builder builder, ItemLike ... items) {
		for (ItemLike item : items) {
			this.entries.add(LootItem.lootTableItem(item).setWeight(weight).apply(builder).build());
		}
		return this;
	}

	public MutableLootPool remove(ItemLike item) {
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (!item.equals(lootItem.item.value())) continue;

			this.entries.remove(entryContainer);
			return this;
		}
		// Failed to remove item from the loot pool.
		return this;
	}

	public MutableLootPool remove(ItemLike... items) {
		final ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;

			for (ItemLike item : items) {
				if (!item.equals(lootItem.item.value())) continue;
				toRemove.add(entryContainer);
			}
		}

		for (LootPoolEntryContainer entryContainer : toRemove) this.entries.remove(entryContainer);
		return this;
	}

	public MutableLootPool remove(TagKey<Item> tag) {
		final ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (!lootItem.item.value().builtInRegistryHolder().is(tag)) continue;
			toRemove.add(entryContainer);
		}

		for (LootPoolEntryContainer entryContainer : toRemove) this.entries.remove(entryContainer);
		return this;
	}

	public MutableLootPool remove(Predicate<Item> predicate) {
		final ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (!predicate.test(lootItem.item.value())) continue;
			toRemove.add(entryContainer);
		}

		for (LootPoolEntryContainer entryContainer : toRemove) this.entries.remove(entryContainer);
		return this;
	}

	public MutableLootPool replace(ItemLike original, ItemLike replacement) {
		for (int i = 0; i < this.entries.size(); i++) {
			final LootPoolEntryContainer entryContainer = this.entries.get(i);
			if (!(entryContainer instanceof LootItem lootItem)) return this;
			if (!original.equals(lootItem.item.value())) continue;

			final MutableLootItem mutableLootItem = new MutableLootItem(lootItem);
			mutableLootItem.setItem(replacement);
			this.entries.set(i, mutableLootItem.build());
		}
		return this;
	}

	/**
	 * Returns if the loot pool contains the given item
	 *
	 * @param item item to check for
	 * @return if item was found
	 */
	public boolean hasItem(Item item) {
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (item.equals(lootItem.item.value())) return true;
		}
		return false;
	}

	/**
	 * Returns if the loot pool contains an item that matches the predicate
	 *
	 * @param predicate condition to check for
	 * @return if item was found
	 */
	public boolean hasItem(Predicate<Item> predicate) {
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (predicate.test(lootItem.item.value())) return true;
		}
		return false;
	}

	/**
	 * Returns if the loot pool contains an item with the given tag
	 *
	 * @param itemTagKey item tag to check for
	 * @return if item was found
	 */
	public boolean hasItem(TagKey<Item> itemTagKey) {
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;
			if (lootItem.item.value().builtInRegistryHolder().is(itemTagKey)) return true;
		}
		return false;
	}

	/**
	 * Returns if the loot pool contains any of the given items
	 *
	 * @param items items to check for
	 * @return if any of the items were found
	 */
	public boolean hasAnyItems(Item... items) {
		for (LootPoolEntryContainer entryContainer : this.entries) {
			if (!(entryContainer instanceof LootItem lootItem)) continue;

			for (Item item : items) {
				if (item.equals(lootItem.item.value())) return true;
			}
		}
		return false;
	}

	/**
	 * Returns if the loot table contains all the given items
	 *
	 * @param items items to check for
	 * @return if all of the items were found
	 */
	public boolean hasAllItems(Item ... items) {
		final Map<Item, Boolean> foundMap = new HashMap<>();
		for (Item item : items) {
			if (foundMap.getOrDefault(item, false)) continue;

			foundMap.put(item, false);
			for (LootPoolEntryContainer entryContainer : this.entries) {
				if (!(entryContainer instanceof LootItem lootItem)) continue;
				if (!item.equals(lootItem.item.value())) continue;
				foundMap.put(item, true);
			}
		}
		return !foundMap.containsValue(false);
	}
}
