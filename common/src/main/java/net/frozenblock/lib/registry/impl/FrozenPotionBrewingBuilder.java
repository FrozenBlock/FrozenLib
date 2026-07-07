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
