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

package net.frozenblock.lib.item.mixin.shovel;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.frozenblock.lib.item.api.shovel.ShovelBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

	@ModifyExpressionValue(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedFace()Lnet/minecraft/core/Direction;",
			ordinal = 0
		)
	)
	public Direction frozenlib$startShovelBehavior(
		Direction original,
		@Local Level level, @Local BlockPos pos, @Local BlockState blockState,
		@Share("frozenLib$isCustomBehavior") LocalBooleanRef isCustomBehavior,
		@Share("frozenLib$direction") LocalRef<Direction> direction,
		@Share("frozenLib$shovelBehavior") LocalRef<ShovelBehaviors.ShovelBehavior> shovelBehavior
	) {
		direction.set(original);
		ShovelBehaviors.ShovelBehavior possibleBehavior = ShovelBehaviors.get(blockState.getBlock());
		if (possibleBehavior != null && possibleBehavior.meetsRequirements(level, pos, original, blockState)) {
			isCustomBehavior.set(true);
			shovelBehavior.set(possibleBehavior);
			return Direction.UP;
		}
		isCustomBehavior.set(false);
		return original;
	}

	@ModifyExpressionValue(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
			ordinal = 0
		)
	)
	public boolean frozenlib$removeOtherBehaviorsA(
		boolean original,
		@Share("frozenLib$isCustomBehavior") LocalBooleanRef isCustomBehavior
	) {
		return !isCustomBehavior.get() && original;
	}

	@ModifyExpressionValue(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;",
			ordinal = 1
		)
	)
	public Block frozenlib$removeOtherBehaviorsB(
		Block original,
		@Share("frozenLib$isCustomBehavior") LocalBooleanRef isCustomBehavior
	) {
		if (isCustomBehavior.get()) {
			return Blocks.AIR;
		}
		return original;
	}

	@Inject(
		method = "useOn",
		at = @At(
			value = "JUMP",
			opcode = Opcodes.IFNULL,
			ordinal = 0
		)
	)
	public void frozenlib$runShovelBehavior(
		UseOnContext context, CallbackInfoReturnable<InteractionResult> info,
		@Local Level level,
		@Local BlockPos pos,
		@Local(ordinal = 0) BlockState blockState,
		@Local(ordinal = 2) LocalRef<BlockState> blockState3,
		@Share("frozenLib$direction") LocalRef<Direction> direction,
		@Share("frozenLib$shovelBehavior") LocalRef<ShovelBehaviors.ShovelBehavior> shovelBehavior
	) {
		ShovelBehaviors.ShovelBehavior runBehavior = shovelBehavior.get();
		if (runBehavior != null) {
			BlockState outputState = runBehavior.getOutputBlockState(blockState);
			runBehavior.onSuccess(level, pos, direction.get(), outputState, blockState);
			blockState3.set(outputState);
		}
	}

}
