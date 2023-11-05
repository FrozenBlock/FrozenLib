/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.recipe.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.JsonOps;
import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.function.Consumer;

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

	@Unique
	@Override
	public @Nullable CompoundTag frozenLib$getTag() {
		return this.tag;
	}

	@WrapOperation(
		method = "save",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
		)
	)
	private void modifySave(Consumer<FinishedRecipe> instance, Object recipe, Operation<?> operation) {
		((ShapedRecipeBuilderExtension) recipe).frozenLib$tag(this.tag);
		operation.call(instance, recipe);
	}

	@Mixin(ShapedRecipeBuilder.Result.class)
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
	}
}
