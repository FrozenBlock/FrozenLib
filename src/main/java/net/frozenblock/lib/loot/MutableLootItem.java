package net.frozenblock.lib.loot;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class MutableLootItem {
	public final List<LootItemCondition> conditions;
	public int weight = LootItem.DEFAULT_WEIGHT;
	public int quality = LootItem.DEFAULT_QUALITY;
	public final List<LootItemFunction> functions;
	public Holder<Item> item;

	public MutableLootItem(LootItem original) {
		this.conditions = original.conditions;
		this.weight = original.weight;
		this.quality = original.quality;
		this.functions = original.functions;
		this.item = original.item;
	}

	public MutableLootItem(Holder<Item> item, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
		this.item = item;
		this.conditions = conditions;
		this.weight = weight;
		this.quality = quality;
		this.functions = functions;
	}

	public MutableLootItem(ItemLike item, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
		this.item = item.asItem().builtInRegistryHolder();
		this.conditions = conditions;
		this.weight = weight;
		this.quality = quality;
		this.functions = functions;
	}

	public LootItem build() {
		return new LootItem(item, weight, quality, conditions, functions);
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
