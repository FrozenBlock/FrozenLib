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

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.frozenblock.lib.recipe.api.FrozenRecipeCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Ingredient.ItemValue.class)
public class ItemValueMixin {

	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", ordinal = 0))
	private static Codec<Ingredient.ItemValue> newCodec(Function<RecordCodecBuilder.Instance<Ingredient.ItemValue>, ? extends App<RecordCodecBuilder.Mu<Ingredient.ItemValue>, Ingredient.ItemValue>> builder) {
		return RecordCodecBuilder.create(instance ->
			instance.group(
				ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(Ingredient.ItemValue::item),
				CompoundTag.CODEC.optionalFieldOf("tag").forGetter(itemValue -> Optional.ofNullable(itemValue.item().getTag()))
			).apply(instance, (item, data) -> {
                data.ifPresent(item::setTag);
                return new Ingredient.ItemValue(item);
			})
		);
	}
}
