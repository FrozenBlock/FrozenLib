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

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.item.api.component.BlockTransformerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockTransformer.class)
public class BlockTransformerMixin {

	@Inject(
		method = "transformBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private static void frozenLib$invokeOnTransformEvent(
		UseOnContext context,
		CallbackInfoReturnable<InteractionResult> info,
		@Local(name = "pos") BlockPos pos,
		@Local(name = "level") Level level,
		@Local(name = "player") Player player,
		@Local(name = "itemInHand") ItemStack itemInHand,
		@Local(name = "oldBlockState") BlockState oldBlockState,
		@Local(name = "updatedShape") BlockState updatedShape
	) {
		BlockTransformerEvents.ON_TRANSFORM.invoker().onTransform(context, player, level, pos, itemInHand, oldBlockState, updatedShape);
	}
}
