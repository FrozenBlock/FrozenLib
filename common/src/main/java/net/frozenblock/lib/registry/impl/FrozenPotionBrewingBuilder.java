package net.frozenblock.lib.registry.impl;

import net.minecraft.core.Holder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

public interface FrozenPotionBrewingBuilder {

	void frozenLib$registerItemRecipe(Item input, Ingredient ingredient, Item output);

	void frozenLib$registerPotionRecipe(Holder<Potion> input, Ingredient ingredient, Holder<Potion> output);

	void frozenLib$registerRecipes(Ingredient ingredient, Holder<Potion> potion);

	FeatureFlagSet frozenLib$getEnabledFeatures();
}
