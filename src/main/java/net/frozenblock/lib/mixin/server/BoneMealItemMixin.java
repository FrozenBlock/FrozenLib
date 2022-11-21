/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.impl.BonemealBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	public void useBonemeal(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState state = level.getBlockState(blockPos);
		Direction direction = context.getClickedFace();
		Direction horizontal = context.getHorizontalDirection();
		if (BonemealBehaviors.BONEMEAL_BEHAVIORS.containsKey(state.getBlock())) {
			if (BonemealBehaviors.BONEMEAL_BEHAVIORS.get(state.getBlock()).bonemeal(context, level, blockPos, state, direction, horizontal) && !level.isClientSide) {
				context.getItemInHand().shrink(1);
				info.setReturnValue(InteractionResult.SUCCESS);
				info.cancel();
			} else {
				info.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
			}
		}
	}

}
