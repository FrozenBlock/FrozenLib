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

package net.frozenblock.lib.axe.mixin;

import java.util.Optional;
import net.frozenblock.lib.axe.api.AxeBehaviors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AxeItem.class)
public class AxeItemMixin {

	@Inject(method = "useOn", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void frozenlib$_axeBehaviors(UseOnContext context, CallbackInfoReturnable<InteractionResult> info, Level level, BlockPos blockPos, Player player, BlockState blockState, Optional<BlockState> optional, Optional <BlockState>optional2, Optional<BlockState> optional3, ItemStack itemStack, Optional<BlockState> optional4) {
		Direction direction = context.getClickedFace();
		Direction horizontal = context.getHorizontalDirection();
		if (AxeBehaviors.AXE_BEHAVIORS.containsKey(blockState.getBlock())) {
			if (AxeBehaviors.AXE_BEHAVIORS.get(blockState.getBlock()).axe(context, level, blockPos, blockState, direction, horizontal)) {
				if (!level.isClientSide) {
					level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));
					if (player != null) {
						context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
					}
					CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockPos, context.getItemInHand());
					info.setReturnValue(InteractionResult.SUCCESS);
				} else {
					info.setReturnValue(InteractionResult.sidedSuccess(true));
				}
			}
		}
	}

}
