/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
				.save(recipeOutput, new ResourceLocation(modID, "dye_" + RecipeProvider.getItemName(item2)));
		}
	}
}
