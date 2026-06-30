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
import java.util.List;
import net.frozenblock.lib.item.api.ItemTooltipAdditionAPI;
import net.frozenblock.lib.tag.api.FrozenLibItemTags;
import net.minecraft.core.TypedInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements TypedInstance<Item> {

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
