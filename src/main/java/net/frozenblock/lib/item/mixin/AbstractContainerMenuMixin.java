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

package net.frozenblock.lib.item.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.item.impl.ItemStackExtension;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

	@WrapOperation(
		method = "doClick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
		)
	)
	private boolean frozenLib$fixIsSameItemSameComponents(ItemStack stackA, ItemStack stackB, Operation<Boolean> original) {
		ItemStackExtension.class.cast(stackA).frozenLib$setCanRemoveTags(true);
		ItemStackExtension.class.cast(stackB).frozenLib$setCanRemoveTags(true);
		final boolean retValue = original.call(stackA, stackB);
		ItemStackExtension.class.cast(stackA).frozenLib$setCanRemoveTags(false);
		ItemStackExtension.class.cast(stackB).frozenLib$setCanRemoveTags(false);
		return retValue;
	}

	@WrapOperation(
		method = "moveItemStackTo",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
		)
	)
	private boolean frozenLib$fixMoveItemStackTo(ItemStack stackA, ItemStack stackB, Operation<Boolean> original) {
		ItemStackExtension.class.cast(stackA).frozenLib$setCanRemoveTags(true);
		ItemStackExtension.class.cast(stackB).frozenLib$setCanRemoveTags(true);
		final boolean retValue = original.call(stackA, stackB);
		ItemStackExtension.class.cast(stackA).frozenLib$setCanRemoveTags(false);
		ItemStackExtension.class.cast(stackB).frozenLib$setCanRemoveTags(false);
		return retValue;
	}
}
