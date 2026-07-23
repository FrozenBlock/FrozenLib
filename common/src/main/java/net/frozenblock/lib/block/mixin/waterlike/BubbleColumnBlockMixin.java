/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnBlockMixin {

	@Inject(method = "getColumnState", at = @At("HEAD"), cancellable = true)
	private static void frozenLib$getColumnState(Block bubbleColumn, BlockState belowState, BlockState occupyState, CallbackInfoReturnable<BlockState> info) {
		if (!WaterLikeBlock.supportsBubbleColumns(belowState)) return;
		final Optional<Direction> dragDirection = WaterLikeBlock.getDirectionAsBubbleColumn(belowState);
		dragDirection.ifPresent(direction -> info.setReturnValue(
			bubbleColumn.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, direction == Direction.DOWN)
		));
	}

	@ModifyExpressionValue(
		method = "updateColumn(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/BubbleColumnBlock;canOccupy(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/block/state/BlockState;)Z",
			ordinal = 1
		)
	)
	private static boolean frozenLib$transferToWaterLikeBubbleColumn(
		boolean original,
		@Local(argsOnly = true) LevelAccessor level,
		@Local(name = "pos") BlockPos.MutableBlockPos pos
	) {
		if (!original) WaterLikeBlock.updateAsBubbleColumn(level, pos, level.getBlockState(pos.immutable().below()));
		return original;
	}
}
