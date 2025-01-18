package net.frozenblock.lib.loot;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import java.util.ArrayList;

public class MutableLootPool {
	public ArrayList<LootPoolEntryContainer> entries = new ArrayList<>();
	public ArrayList<LootItemCondition> conditions = new ArrayList<>();
	public ArrayList<LootItemFunction> functions = new ArrayList<>();
	public NumberProvider rolls = ConstantValue.exactly(1.0F);
	public NumberProvider bonusRolls = ConstantValue.exactly(0.0F);

	public MutableLootPool(LootPool lootPool) {
		entries.addAll(lootPool.entries);
		conditions.addAll(lootPool.conditions);
		functions.addAll(lootPool.functions);
		rolls = lootPool.rolls;
		bonusRolls = lootPool.bonusRolls;
	}

	public LootPool build() {
		LootPool.Builder builder = LootPool.lootPool();
//		entries.forEach(entry -> ((LootPoolBuilderInterface) builder).wilderWild$add(entry));
		entries.forEach(builder.entries::add);
//		conditions.forEach(condition -> ((LootPoolBuilderInterface) builder).wilderWild$when(condition));
		conditions.forEach(builder.conditions::add);
//		functions.forEach(function -> ((LootPoolBuilderInterface) builder).wilderWild$apply(function));
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
}
