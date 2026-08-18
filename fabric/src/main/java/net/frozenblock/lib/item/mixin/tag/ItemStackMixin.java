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

package net.frozenblock.lib.item.mixin.tag;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.tag.api.FrozenLibItemTags;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemStack.class)
public class ItemStackMixin { // In common mixins.json

	@ModifyExpressionValue(
		method = "addDetailsToTooltip",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/item/Items;SPAWNER:Lnet/minecraft/world/item/Item;",
				opcode = Opcodes.GETSTATIC
			)
		)
	)
	private boolean frozenLib$showSpawnerTooltip(boolean original) {
		return original || ItemStack.class.cast(this).is(FrozenLibItemTags.SPAWNER);
	}
}
