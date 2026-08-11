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

package net.frozenblock.lib.item.mixin.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.item.api.recipe.RecipeExportNamespaceFix;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(RecipeBuilder.class)
public interface RecipeBuilderMixin {

	@WrapOperation(
		method = {
			"save(Lnet/minecraft/data/recipes/RecipeOutput;)V",
			"save(Lnet/minecraft/data/recipes/RecipeOutput;Ljava/lang/String;)V"
		},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/data/recipes/RecipeBuilder;save(Lnet/minecraft/data/recipes/RecipeOutput;Lnet/minecraft/resources/ResourceKey;)V"
		)
	)
	default void frozenLib$save(RecipeBuilder instance, RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeResourceKey, Operation<Void> original) {
		final Optional<String> modId = RecipeExportNamespaceFix.getCurrentGeneratingModId();
		if (modId.isPresent()) {
			recipeResourceKey = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(modId.get(), recipeResourceKey.identifier().getPath()));
		}

		original.call(instance, recipeOutput, recipeResourceKey);
	}
}
