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

package net.frozenblock.lib.item.mixin.bonemeal;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.item.api.bonemeal.BonemealBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(
		method = "growCrop",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
		),
		cancellable = true
	)
    private static void frozenLib$runBonemeal(
		ItemStack stack, Level world, BlockPos pos, CallbackInfoReturnable<Boolean> info,
		@Local(ordinal = 0) BlockState blockState
	) {
		BonemealBehaviors.BonemealBehavior bonemealBehavior = BonemealBehaviors.get(blockState.getBlock());
        if (bonemealBehavior != null && bonemealBehavior.meetsRequirements(world, pos, blockState)) {
			if (world instanceof ServerLevel serverLevel) {
				if (bonemealBehavior.isBonemealSuccess(world, world.random, pos, blockState)) {
					bonemealBehavior.performBonemeal(serverLevel, world.random, pos, blockState);
				}

				stack.shrink(1);
			}

			info.setReturnValue(true);
        }
    }

}
