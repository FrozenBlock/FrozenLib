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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.frozenblock.lib.item.api.component.ItemTooltipAdditionAPI;
import net.frozenblock.lib.item.api.removable.RemovableDataComponents;
import net.frozenblock.lib.item.impl.ItemStackExtension;
import net.frozenblock.lib.tag.api.FrozenLibItemTags;
import net.minecraft.core.TypedInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements TypedInstance<Item>, ItemStackExtension {

	@Shadow
	public abstract Item getItem();

	@ModifyExpressionValue(
		method = "causeUseVibration",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/component/UseEffects;interactVibrations()Z"
		)
	)
	private boolean preventStartingGameEvent(boolean original) {
		return original && !this.is(FrozenLibItemTags.NO_USE_GAME_EVENTS);
	}

	@Unique
	private boolean frozenLib$canRemoveTags = false;

	@Inject(at = @At("TAIL"), method = "inventoryTick")
	public void frozenLib$removeTags(Level level, Entity owner, EquipmentSlot slot, CallbackInfo info) {
		final ItemStack stack = ItemStack.class.cast(this);
		RemovableDataComponents.fixEmptyComponentsAndTags(stack);
	}

	@WrapOperation(
		method = "isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"
		)
	)
	private static boolean frozenLib$removeTagsAndCompare(
		Object a, Object b, Operation<Boolean> original,
		ItemStack left, ItemStack right
	) {
		if (left.frozenLib$canRemoveTags()) RemovableDataComponents.fixEmptyComponentsAndTags(left);
		if (right.frozenLib$canRemoveTags()) RemovableDataComponents.fixEmptyComponentsAndTags(right);
		return original.call(a, b);
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

	@ModifyReturnValue(
		method = "getTooltipLines",
		at = @At(
			value = "RETURN",
			ordinal = 1
		)
	)
	public List<Component> frozenLib$appendAdditionalTooltips(List<Component> original) {
		ItemTooltipAdditionAPI.getTooltipsForItemStack(ItemStack.class.cast(this)).ifPresent(original::addAll);
		return original;
	}
}
