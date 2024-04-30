/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.shovel.mixin;

import net.frozenblock.lib.shovel.api.ShovelBehaviors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedFace()Lnet/minecraft/core/Direction;", shift = At.Shift.BEFORE, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void frozenlib$_shovelBehaviors(UseOnContext context, CallbackInfoReturnable<InteractionResult> info, Level level, BlockPos blockPos, BlockState blockState) {
		Direction direction = context.getClickedFace();
		Direction horizontal = context.getHorizontalDirection();
		if (ShovelBehaviors.SHOVEL_BEHAVIORS.containsKey(blockState.getBlock())) {
			if (ShovelBehaviors.SHOVEL_BEHAVIORS.get(blockState.getBlock()).shovel(context, level, blockPos, blockState, direction, horizontal)) {
				if (!level.isClientSide) {
					Player player = context.getPlayer();
					level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));
					if (player != null) {
						context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
					}
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockPos, context.getItemInHand());
					info.setReturnValue(InteractionResult.SUCCESS);
				} else {
					info.setReturnValue(InteractionResult.sidedSuccess(true));
				}
			}
		}
	}

}
