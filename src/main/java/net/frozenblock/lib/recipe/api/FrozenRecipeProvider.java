/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.recipe.api;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class FrozenRecipeProvider {
	private FrozenRecipeProvider() {}

	public static void woodenButton(RecipeOutput recipeOutput, ItemLike button, ItemLike material) {
		RecipeProvider.buttonBuilder(button, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_button").save(recipeOutput);
	}

	public static void woodenDoor(RecipeOutput recipeOutput, ItemLike door, ItemLike material) {
		RecipeProvider.doorBuilder(door, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_door").save(recipeOutput);
	}

	public static void woodenFence(RecipeOutput recipeOutput, ItemLike fence, ItemLike material) {
		RecipeProvider.fenceBuilder(fence, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_fence").save(recipeOutput);
	}

	public static void woodenFenceGate(RecipeOutput recipeOutput, ItemLike fenceGate, ItemLike material) {
		RecipeProvider.fenceGateBuilder(fenceGate, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_fence_gate").save(recipeOutput);
	}


	public static void woodenPressurePlace(RecipeOutput recipeOutput, ItemLike pressurePlate, ItemLike material) {
		RecipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_pressure_plate").save(recipeOutput);
	}

	public static void woodenSlab(RecipeOutput recipeOutput, ItemLike slab, ItemLike material) {
		RecipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_slab").save(recipeOutput);
	}

	public static void woodenStairs(RecipeOutput recipeOutput, ItemLike stairs, ItemLike material) {
		RecipeProvider.stairBuilder(stairs, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_stairs").save(recipeOutput);
	}

	public static void woodenTrapdoor(RecipeOutput recipeOutput, ItemLike trapdoor, ItemLike material) {
		RecipeProvider.trapdoorBuilder(trapdoor, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_trapdoor").save(recipeOutput);
	}
}
