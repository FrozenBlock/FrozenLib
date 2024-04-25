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

package net.frozenblock.lib.recipe.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShapedRecipeBuilder.class)
public class ShapedRecipeBuilderMixin implements ShapedRecipeBuilderExtension {

	@Unique
	@Nullable
	private CompoundTag tag;

	@Unique
	@Override
	public ShapedRecipeBuilder frozenLib$tag(@Nullable CompoundTag tag) {
		this.tag = tag;
		return (ShapedRecipeBuilder) (Object) this;
	}

	@WrapOperation(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/data/recipes/RecipeOutput;accept(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/advancements/AdvancementHolder;)V"
		)
	)
	private void modifySave(RecipeOutput instance, ResourceLocation recipeId, Recipe<?> recipe, AdvancementHolder holder, Operation<ShapedRecipe> operation) {
		((ShapedRecipeBuilderExtension) recipe).frozenLib$tag(this.tag);
		operation.call(instance, recipeId, recipe, holder);
	}

	/*@Mixin(ShapedRecipeBuilder.Result.class)
	private static class ResultMixin implements ShapedRecipeBuilderExtension {

		@Unique
		@Nullable
		private CompoundTag tag;

		@Override
		public ShapedRecipeBuilder frozenLib$tag(CompoundTag tag) {
			this.tag = tag;
			return null;
		}

		@Override
		public @Nullable CompoundTag frozenLib$getTag() {
			return this.tag;
		}

		@Inject(method = "serializeRecipeData", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
		private void addTagData(JsonObject json, CallbackInfo ci, JsonArray jsonArray, JsonObject jsonObject, JsonObject jsonObject2) {
			if (this.tag != null) {
				jsonObject2.add("tag", CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, this.tag).getOrThrow(false, str -> {}));
			}
		}
	}*/
}
