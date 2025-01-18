package net.frozenblock.lib.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MutableLootTable {
	private ArrayList<MutableLootPool> pools = new ArrayList<>();
	private ArrayList<LootItemFunction> functions = new ArrayList<>();
	private LootContextParamSet paramSet = LootTable.DEFAULT_PARAM_SET;
	private ResourceLocation randomSequence;

	public MutableLootTable(LootTable table) {
		pools = createLootPools(table.pools);
		functions.addAll(table.functions);
		paramSet = table.getParamSet();
		randomSequence = table.randomSequence.orElse(null);
	}

	public static @Nullable MutableLootTable getMutable(ResourceKey<LootTable> lootTableKey, ResourceKey<LootTable> id, LootTable lootTable) {
		if (lootTableKey.equals(id)) {
			return new MutableLootTable(lootTable);
		} else return null;
	}

	public static @Nullable MutableLootTable getMutable(ResourceKey<LootTable> lootTableKey, ResourceKey<LootTable> id, LootTable lootTable, LootTableSource source) {
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
	 * Runs the consumer on each pool
	 *
	 * @return this
	 */
	public MutableLootTable modifyPools(Consumer<MutableLootPool> consumer) {
		pools.forEach(consumer);
		return this;
	}

	/**
	 * Runs the consumer on each pool that matches the condition
	 *
	 * @return this
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
	 * @param lootPoolList loot pools to copy
	 * @return array list of converted loot pools from input
	 */
	private static ArrayList<MutableLootPool> createLootPools(List<LootPool> lootPoolList) {
		ArrayList<MutableLootPool> lootPools = new ArrayList<>();
		lootPoolList.forEach(pool -> lootPools.add(new MutableLootPool(pool)));
		return lootPools;
	}

	/**
	 * Returns if a pool has the given item
	 *
	 * @param item item to check for
	 * @return predicate that checks if the pool has the given item
	 */
	public static Predicate<MutableLootPool> has(Item item) {
		return lootPool -> lootPool.hasItem(item);
	}

	/**
	 * Returns if a pool has any of the given items
	 *
	 * @param items items to check for
	 * @return predicate that checks if the pool has any of the given items
	 */
	public static Predicate<MutableLootPool> hasAny(Item... items) {
		return lootPool -> lootPool.hasAnyItems(items);
	}

	/**
	 * Returns if a pool has all the given items
	 *
	 * @param items items to check for
	 * @return predicate that checks if the pool has all the given items
	 */
	public static Predicate<MutableLootPool> hasAll(Item... items) {
		return lootPool -> lootPool.hasAllItems(items);
	}
}
