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

package net.frozenblock.lib.recipe.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.frozenblock.lib.recipe.api.ShapelessRecipeBuilderExtension;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapelessRecipeBuilder.class)
public class ShapelessRecipeBuilderMixin implements ShapelessRecipeBuilderExtension {

	@Unique
	@Nullable
	private DataComponentPatch patch;

	@Unique
	@Override
	public ShapelessRecipeBuilder frozenLib$patch(@Nullable DataComponentPatch patch) {
		this.patch = patch;
		return (ShapelessRecipeBuilder) (Object) this;
	}

	@WrapOperation(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/data/recipes/RecipeOutput;accept(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/advancements/AdvancementHolder;)V"
		)
	)
	private void modifySave(RecipeOutput instance, ResourceLocation recipeId, Recipe<?> recipe, AdvancementHolder holder, Operation<ShapedRecipe> operation) {
		((ShapedRecipeBuilderExtension) recipe).frozenLib$patch(this.patch);
		operation.call(instance, recipeId, recipe, holder);
	}
}
