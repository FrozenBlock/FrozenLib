/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import java.util.stream.Stream;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public final class FrozenRecipeProvider {

	public static void woodenButton(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike button, ItemLike material) {
		recipeProvider.buttonBuilder(button, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_button").save(recipeOutput);
	}

	public static void woodenDoor(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike door, ItemLike material) {
		recipeProvider.doorBuilder(door, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_door").save(recipeOutput);
	}

	public static void woodenFence(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike fence, ItemLike material) {
		recipeProvider.fenceBuilder(fence, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_fence").save(recipeOutput);
	}

	public static void woodenFenceGate(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike fenceGate, ItemLike material) {
		recipeProvider.fenceGateBuilder(fenceGate, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_fence_gate").save(recipeOutput);
	}

	public static void woodenPressurePlace(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike pressurePlate, ItemLike material) {
		recipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_pressure_plate").save(recipeOutput);
	}

	public static void woodenSlab(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike slab, ItemLike material) {
		recipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_slab").save(recipeOutput);
	}

	public static void woodenStairs(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike stairs, ItemLike material) {
		recipeProvider.stairBuilder(stairs, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_stairs").save(recipeOutput);
	}

	public static void woodenTrapdoor(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike trapdoor, ItemLike material) {
		recipeProvider.trapdoorBuilder(trapdoor, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_trapdoor").save(recipeOutput);
	}

	public static void woodenSign(RecipeProvider recipeProvider, RecipeOutput recipeOutput, ItemLike sign, ItemLike material) {
		recipeProvider.signBuilder(sign, Ingredient.of(material))
			.unlockedBy("has_planks", recipeProvider.has(material))
			.group("wooden_sign").save(recipeOutput);
	}

	public static void colorWithDyes(RecipeProvider recipeProvider, RecipeOutput recipeOutput, List<Item> list, List<Item> list2, @Nullable Item item, String group, RecipeCategory recipeCategory) {
		for (int i = 0; i < list.size(); ++i) {
			final Item item2 = list.get(i);
			final Item item3 = list2.get(i);
			Stream<Item> stream = list2.stream().filter((item2x) -> !item2x.equals(item3));
			if (item != null) stream = Stream.concat(stream, Stream.of(item));

			recipeProvider.shapeless(recipeCategory, item3)
				.requires(item2)
				.requires(Ingredient.of(stream))
				.group(group).unlockedBy(
					"has_needed_dye",
					recipeProvider.has(item2))
				.save(recipeOutput, "dye_" + RecipeProvider.getItemName(item3));
		}
	}

}
