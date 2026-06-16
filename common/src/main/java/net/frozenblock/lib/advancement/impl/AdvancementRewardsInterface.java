package net.frozenblock.lib.advancement.impl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;
import java.util.List;

public interface AdvancementRewardsInterface {
	List<ResourceKey<LootTable>> frozenLib$getLoot();
	void frozenLib$setLoot(List<ResourceKey<LootTable>> loot);

	List<ResourceKey<Recipe<?>>> frozenLib$getRecipes();
	void frozenLib$setRecipes(List<ResourceKey<Recipe<?>>> recipes);
}
