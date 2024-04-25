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

package net.frozenblock.lib.item.mixin;

import net.frozenblock.lib.item.api.removable.RemovableItemTags;
import net.frozenblock.lib.item.impl.ItemStackExtension;
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
		for (String key : RemovableItemTags.keys()) {
			if (RemovableItemTags.canRemoveTag(key, level, entity, slot, selected)) {
				stack.removeTagKey(key);
			}
		}
	}

	@Inject(method = "isSameItemSameTags", at = @At("HEAD"))
	private static void frozenLib$removeTagsAndCompare(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> info) {
		var extendedLeft = ItemStackExtension.class.cast(left);
		var extendedRight = ItemStackExtension.class.cast(right);


		if (extendedLeft.frozenLib$canRemoveTags()) {
			frozenLib$fixEmptyTags(left);
			extendedLeft.frozenLib$setCanRemoveTags(false);
		}

		if (extendedRight.frozenLib$canRemoveTags()) {
			frozenLib$fixEmptyTags(right);
			extendedRight.frozenLib$setCanRemoveTags(false);
		}
	}

	@Unique
	private static void frozenLib$fixEmptyTags(ItemStack stack) {
		for (String key : RemovableItemTags.keys()) {
			if (RemovableItemTags.shouldRemoveTagOnStackMerge(key)) {
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
