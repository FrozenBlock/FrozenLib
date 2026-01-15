/*
 * Copyright (C) 2024-2026 FrozenBlock
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

	public static void woodenButton(RecipeProvider provider, RecipeOutput output, ItemLike button, ItemLike material) {
		provider.buttonBuilder(button, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_button").save(output);
	}

	public static void woodenDoor(RecipeProvider provider, RecipeOutput output, ItemLike door, ItemLike material) {
		provider.doorBuilder(door, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_door").save(output);
	}

	public static void woodenFence(RecipeProvider provider, RecipeOutput output, ItemLike fence, ItemLike material) {
		provider.fenceBuilder(fence, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_fence").save(output);
	}

	public static void woodenFenceGate(RecipeProvider provider, RecipeOutput output, ItemLike fenceGate, ItemLike material) {
		provider.fenceGateBuilder(fenceGate, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_fence_gate").save(output);
	}

	public static void woodenPressurePlace(RecipeProvider provider, RecipeOutput output, ItemLike pressurePlate, ItemLike material) {
		provider.pressurePlateBuilder(RecipeCategory.REDSTONE, pressurePlate, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_pressure_plate").save(output);
	}

	public static void woodenSlab(RecipeProvider provider, RecipeOutput output, ItemLike slab, ItemLike material) {
		provider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_slab").save(output);
	}

	public static void woodenStairs(RecipeProvider provider, RecipeOutput output, ItemLike stairs, ItemLike material) {
		provider.stairBuilder(stairs, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_stairs").save(output);
	}

	public static void woodenTrapdoor(RecipeProvider provider, RecipeOutput output, ItemLike trapdoor, ItemLike material) {
		provider.trapdoorBuilder(trapdoor, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_trapdoor").save(output);
	}

	public static void woodenSign(RecipeProvider provider, RecipeOutput output, ItemLike sign, ItemLike material) {
		provider.signBuilder(sign, Ingredient.of(material))
			.unlockedBy("has_planks", provider.has(material))
			.group("wooden_sign").save(output);
	}

	public static void colorWithDyes(RecipeProvider provider, RecipeOutput output, List<Item> list, List<Item> list2, @Nullable Item item, String group, RecipeCategory recipeCategory) {
		for (int i = 0; i < list.size(); ++i) {
			final Item item2 = list.get(i);
			final Item item3 = list2.get(i);
			Stream<Item> stream = list2.stream().filter((item2x) -> !item2x.equals(item3));
			if (item != null) stream = Stream.concat(stream, Stream.of(item));

			provider.shapeless(recipeCategory, item3)
				.requires(item2)
				.requires(Ingredient.of(stream))
				.group(group).unlockedBy(
					"has_needed_dye",
					provider.has(item2))
				.save(output, "dye_" + RecipeProvider.getItemName(item3));
		}
	}

}
