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

package net.frozenblock.lib.item.mixin.component;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.item.api.component.DamageOnConsume;
import net.frozenblock.lib.item.api.component.FrozenLibDataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {

	@ModifyExpressionValue(
		method = "finishUsingItem",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/component/Consumable;onConsume(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
		)
	)
	private ItemStack frozenLib$damageOnConsume(
		ItemStack original,
		ItemStack itemStack, Level level, LivingEntity entity
	) {
		final DamageOnConsume damageOnConsume = itemStack.get(FrozenLibDataComponents.DAMAGE_ON_CONSUME.get());
		if (damageOnConsume != null) damageOnConsume.onConsume(level, entity);
		return original;
	}
}
