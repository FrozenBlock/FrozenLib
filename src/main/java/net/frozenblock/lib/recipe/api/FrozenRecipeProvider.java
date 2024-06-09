/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.recipe.api;

import java.util.List;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

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

	public static void woodenSign(RecipeOutput recipeOutput, ItemLike sign, ItemLike material) {
		RecipeProvider.signBuilder(sign, Ingredient.of(material))
			.unlockedBy("has_planks", RecipeProvider.has(material))
			.group("wooden_sign").save(recipeOutput);
	}

	public static void colorWithDyes(RecipeOutput recipeOutput, @NotNull List<Item> dyes, List<Item> dyeableItems, String group, RecipeCategory recipeCategory, String modID) {
		for(int i = 0; i < dyes.size(); ++i) {
			Item item = dyes.get(i);
			Item item2 = dyeableItems.get(i);
			ShapelessRecipeBuilder.shapeless(recipeCategory, item2)
				.requires(item)
				.requires(Ingredient.of(dyeableItems.stream().filter(item2x -> !item2x.equals(item2)).map(ItemStack::new)))
				.group(group)
				.unlockedBy("has_needed_dye", RecipeProvider.has(item))
				.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(modID, "dye_" + RecipeProvider.getItemName(item2)));
		}
	}
}
