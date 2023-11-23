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

package net.frozenblock.lib.item.mixin;

import net.frozenblock.lib.item.api.RemoveableItemTags;
import net.frozenblock.lib.item.impl.ItemStackExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public final class ItemStackMixin implements ItemStackExtension {

	@Unique
	private boolean frozenLib$canRemoveTags = false;

	@Inject(at = @At("TAIL"), method = "inventoryTick")
	public void frozenLib$removeTags(Level level, Entity entity, int slot, boolean selected, CallbackInfo info) {
		ItemStack stack = ItemStack.class.cast(this);
		for (String key : RemoveableItemTags.keys()) {
			if (RemoveableItemTags.canRemoveTag(key, level, entity, slot, selected)) {
				stack.removeTagKey(key);
			}
		}
	}

	@Inject(method = "isSameItemSameTags", at = @At("HEAD"))
	private static void frozenLib$removeTagsAndCompare(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> info) {
		var extendedLeft = ItemStackExtension.class.cast(left);
		var extendedRight = ItemStackExtension.class.cast(right);


		if (extendedLeft.frozenLib$canRemoveTags()) {
			CompoundTag lTag = left.getTag();
			frozenLib$fixEmptyTags(left);
			extendedLeft.frozenLib$setCanRemoveTags(false);
		}

		if (extendedRight.frozenLib$canRemoveTags()) {
			CompoundTag rTag = right.tag;
			frozenLib$fixEmptyTags(right);
			extendedRight.frozenLib$setCanRemoveTags(false);
		}
	}

	@Unique
	private static void frozenLib$fixEmptyTags(ItemStack stack) {
		for (String key : RemoveableItemTags.keys()) {
			if (RemoveableItemTags.shouldRemoveTagOnStackMerge(key)) {
				stack.removeTagKey(key);
			}
		}
	}

	@Unique
	@Override
	public boolean frozenLib$canRemoveTags() {
		return this.frozenLib$canRemoveTags;
	}

	@Unique
	@Override
	public void frozenLib$setCanRemoveTags(boolean canRemoveTags) {
		this.frozenLib$canRemoveTags = canRemoveTags;
	}
}
