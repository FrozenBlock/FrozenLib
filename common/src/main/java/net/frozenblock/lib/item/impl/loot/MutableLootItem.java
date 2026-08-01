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

package net.frozenblock.lib.item.impl.loot;

import io.netty.util.internal.UnstableApi;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.item.mixin.loot.LootPoolEntryContainerAccessor;
import net.frozenblock.lib.item.mixin.loot.UniformContainerBaseAccessor;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

@UnstableApi
public class MutableLootItem {
	public final ArrayList<Holder<LootItemCondition>> conditions = new ArrayList<>();
	public int weight;
	public int quality;
	public final ArrayList<Holder<LootItemFunction>> functions = new ArrayList<>();
	public Holder<Item> item;

	public MutableLootItem(LootItem original) {
		((LootPoolEntryContainerAccessor) original).frozenLib$getCondition().ifPresent(this.conditions::add);
		((LootPoolEntryContainerAccessor) original).frozenLib$getModifier().ifPresent(this.functions::add);
		this.weight = ((UniformContainerBaseAccessor) original).frozenLib$getWeight();
		this.quality = ((UniformContainerBaseAccessor) original).frozenLib$getQuality();
		this.item = original.item;
	}

	public MutableLootItem(Holder<Item> item, int weight, int quality, List<Holder<LootItemCondition>> conditions, List<Holder<LootItemFunction>> functions) {
		this.item = item;
		this.conditions.addAll(conditions);
		this.weight = weight;
		this.quality = quality;
		this.functions.addAll(functions);
	}

	public MutableLootItem(ItemLike item, int weight, int quality, List<Holder<LootItemCondition>> conditions, List<Holder<LootItemFunction>> functions) {
		this(item.asItem().builtInRegistryHolder(), weight, quality, conditions, functions);
	}

	public LootItem build() {
		final UniformContainerBase.Builder<?> builder = LootItem.lootTableItem(this.item.value());
		builder.setWeight(this.weight);
		builder.setQuality(this.quality);
		this.conditions.forEach(builder::when);
		this.functions.forEach(builder::apply);
		return (LootItem) builder.build();
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public Holder<Item> getItemHolder() {
		return item;
	}

	public Item getItem() {
		return item.value();
	}

	public void setItem(Holder<Item> item) {
		this.item = item;
	}

	public void setItem(ItemLike item) {
		this.item = item.asItem().builtInRegistryHolder();
	}
}
