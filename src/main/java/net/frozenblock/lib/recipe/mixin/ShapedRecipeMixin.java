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

package net.frozenblock.lib.recipe.mixin;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.JsonOps;
import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Adds {@link CompoundTag} data support to crafting recipes.
 * @since 1.4.1
 */
@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin implements ShapedRecipeBuilderExtension {

	@Shadow
	@Final
	ItemStack result;

	@Override
	public ShapedRecipeBuilder frozenLib$tag(@Nullable CompoundTag tag) {
		this.result.setTag(tag);
		return null;
	}

	@ModifyReturnValue(method = "itemStackFromJson", at = @At("RETURN"))
	private static ItemStack itemStackFromJson(ItemStack stack, JsonObject stackObject) {
		if (stackObject.has("tag"))
			stack.setTag(CompoundTag.CODEC.decode(JsonOps.INSTANCE, stackObject.get("tag")).getOrThrow(false, error -> {
			}).getFirst());
		return stack;
	}
}
