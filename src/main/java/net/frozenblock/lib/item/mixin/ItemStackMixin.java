/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.frozenblock.lib.item.api.ItemTooltipAdditionAPI;
import net.frozenblock.lib.item.api.removable.RemovableDataComponents;
import net.frozenblock.lib.item.api.removable.RemovableItemTags;
import net.frozenblock.lib.item.impl.ItemStackExtension;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
		return original && !this.is(FrozenItemTags.NO_USE_GAME_EVENTS);
	}

	@Unique
	private boolean frozenLib$canRemoveTags = false;

	@Inject(at = @At("TAIL"), method = "inventoryTick")
	public void frozenLib$removeTags(Level level, Entity entity, EquipmentSlot slot, CallbackInfo info) {
		final ItemStack stack = ItemStack.class.cast(this);

		// Removable Item Tags
		CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> {
			for (String key : RemovableItemTags.keys()) {
				if (RemovableItemTags.canRemoveTag(key, level, entity, slot)) compound.remove(key);
			}
		});

		// Removable Data Components
		for (Holder<DataComponentType<?>> component : RemovableDataComponents.keys()) {
			final DataComponentType<?> value = component.value();
			if (RemovableDataComponents.canRemoveComponent(value, level, entity, slot)) stack.remove(value);
		}
	}

	@WrapOperation(
		method = "isSameItemSameComponents",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"
		)
	)
	private static boolean frozenLib$removeTagsAndCompare(
		Object a, Object b, Operation<Boolean> original,
		ItemStack left, ItemStack right
	) {
		if (ItemStackExtension.class.cast(left).frozenLib$canRemoveTags()) frozenLib$fixEmptyTags(left);
		if (ItemStackExtension.class.cast(right).frozenLib$canRemoveTags()) frozenLib$fixEmptyTags(right);
		return original.call(a, b);
	}

	@Unique
	private static void frozenLib$fixEmptyTags(ItemStack stack) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, compound -> {
			for (String key : RemovableItemTags.keys()) {
				if (RemovableItemTags.shouldRemoveTagOnStackMerge(key)) compound.remove(key);
			}
		});

		for (Holder<DataComponentType<?>> holder : RemovableDataComponents.keys()) {
			final var value = holder.value();
			if (RemovableDataComponents.shouldRemoveComponentOnStackMerge(value)) stack.remove(value);
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
