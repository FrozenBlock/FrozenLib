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

package net.frozenblock.lib.item.mixin.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.item.api.removable.RemovableDataComponents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	/**
	 * @reason NeoForge adds a new version of {@code isSameItemSameComponents} with support for {@code ItemStackTemplate}s.
	 */
	@WrapOperation(
		method = "isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStackTemplate;)Z",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/component/PatchedDataComponentMap;patchEquals(Lnet/minecraft/core/component/DataComponentPatch;)Z"
		)
	)
	private static boolean frozenLib$removeTagsAndCompare(
		PatchedDataComponentMap instance, DataComponentPatch dataComponentPatch, Operation<Boolean> original
	) {
		RemovableDataComponents.fixEmptyComponentsAndTags(instance);
		return original.call(instance, dataComponentPatch);
	}
}
