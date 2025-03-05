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
import java.util.function.Predicate;
import io.netty.util.internal.UnstableApi;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

@UnstableApi
public class MutableLootPool {
	public ArrayList<LootPoolEntryContainer> entries = new ArrayList<>();
	public ArrayList<LootItemCondition> conditions = new ArrayList<>();
	public ArrayList<LootItemFunction> functions = new ArrayList<>();
	public NumberProvider rolls;
	public NumberProvider bonusRolls;

	public MutableLootPool(@NotNull LootPool lootPool) {
		entries.addAll(lootPool.entries);
		conditions.addAll(lootPool.conditions);
		functions.addAll(lootPool.functions);
		rolls = lootPool.rolls;
		bonusRolls = lootPool.bonusRolls;
	}

	public LootPool build() {
		LootPool.Builder builder = LootPool.lootPool();
		entries.forEach(builder.entries::add);
		conditions.forEach(builder.conditions::add);
		functions.forEach(builder.functions::add);
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
		entries.add(LootItem.lootTableItem(item).setWeight(weight).apply(builder).build());
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
	public MutableLootPool addAll(int weight, LootItemFunction.Builder builder, ItemLike @NotNull ... items) {
		for (ItemLike item : items) {
			entries.add(LootItem.lootTableItem(item).setWeight(weight).apply(builder).build());
		}
		return this;
	}

	public MutableLootPool remove(ItemLike item) {
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (item.equals(lootItem.item.value())) {
					entries.remove(entryContainer);
					return this;
				}
			}
		}
		// Failed to remove item from the loot pool.
		return this;
	}

	public MutableLootPool remove(ItemLike... items) {
		ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				for (ItemLike item : items) {
					if (item.equals(lootItem.item.value())) {
						toRemove.add(entryContainer);
					}
				}
			}
		}
		for (LootPoolEntryContainer entryContainer : toRemove) {
			entries.remove(entryContainer);
		}
		return this;
	}

	public MutableLootPool remove(TagKey<Item> tag) {
		ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (lootItem.item.value().builtInRegistryHolder().is(tag)) {
					toRemove.add(entryContainer);
				}
			}
		}
		for (LootPoolEntryContainer entryContainer : toRemove) {
			entries.remove(entryContainer);
		}
		return this;
	}

	public MutableLootPool remove(Predicate<Item> predicate) {
		ArrayList<LootPoolEntryContainer> toRemove = new ArrayList<>();
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (predicate.test(lootItem.item.value())) {
					toRemove.add(entryContainer);
				}
			}
		}
		for (LootPoolEntryContainer entryContainer : toRemove) {
			entries.remove(entryContainer);
		}
		return this;
	}

	public MutableLootPool replace(ItemLike original, ItemLike replacement) {
		for (int i = 0; i < entries.size(); i++) {
			LootPoolEntryContainer entryContainer = entries.get(i);
			if (entryContainer instanceof LootItem lootItem) {
				if (original.equals(lootItem.item.value())) {
					MutableLootItem mutableLootItem = new MutableLootItem(lootItem);
					mutableLootItem.setItem(replacement);
					entries.set(i, mutableLootItem.build());
				}
			}
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
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (item.equals(lootItem.item.value())) return true;
			}
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
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (predicate.test(lootItem.item.value())) return true;
			}
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
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				if (lootItem.item.value().builtInRegistryHolder().is(itemTagKey)) return true;
			}
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
		for (LootPoolEntryContainer entryContainer : entries) {
			if (entryContainer instanceof LootItem lootItem) {
				for (Item item : items) {
					if (item.equals(lootItem.item.value())) return true;
				}
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
	public boolean hasAllItems(Item @NotNull ... items) {
		for (Item item : items) {
			boolean found = false;
			for (LootPoolEntryContainer entryContainer : entries) {
				if (entryContainer instanceof LootItem lootItem) {
					if (item.equals(lootItem.item.value())) {
						found = true;
						break;
					}
				}
			}
			if (!found) return false;
		}
		return true;
	}
}
