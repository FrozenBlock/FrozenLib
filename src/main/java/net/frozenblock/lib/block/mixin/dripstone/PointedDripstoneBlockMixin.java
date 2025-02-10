/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.block.mixin.dripstone;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.frozenblock.lib.block.api.dripstone.DripstoneDripApi;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

	@Inject(
		method = "method_33279",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	private static void frozenLib$getFluidAboveStalactite(
		Level level, BlockPos pos, CallbackInfoReturnable<PointedDripstoneBlock.FluidInfo> info,
		@Local(ordinal = 1) BlockPos blockPos, @Local BlockState blockState
	) {
		if (blockPos != null && blockState != null) {
			if (DripstoneDripApi.containsWaterDrip(blockState.getBlock()) && !level.dimensionType().ultraWarm()) {
				info.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.WATER, blockState));
			} else if (DripstoneDripApi.containsLavaDrip(blockState.getBlock())) {
				info.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.LAVA, blockState));
			}
		}
	}

	@Inject(
		method = "maybeTransferFluid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true
	)
	private static void frozenLib$maybeTransferFluid(
		BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo info,
		@Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 1) BlockPos blockPos
	) {
		if (optional.isPresent()) {
			PointedDripstoneBlock.FluidInfo fluidInfo = optional.get();
			Block block = optional.get().sourceState().getBlock();
			if (DripstoneDripApi.containsWaterDrip(block) && fluid == Fluids.WATER) {
				DripstoneDripApi.runWaterDripsIfPresent(block, level, blockPos, fluidInfo);
				info.cancel();
			} else if (DripstoneDripApi.containsLavaDrip(block) && fluid == Fluids.LAVA) {
				DripstoneDripApi.runLavaDripsIfPresent(block, level, blockPos, fluidInfo);
				info.cancel();
			}
		}
	}

	@Inject(method = "method_33274", at = @At(value = "HEAD"), cancellable = true)
	private static void frozenLib$dripOnNewAllowedBlocks(Fluid fluid, BlockState blockState, CallbackInfoReturnable<Boolean> info) {
		if (blockState.is(FrozenBlockTags.DRIPSTONE_CAN_DRIP_ON)) {
			info.setReturnValue(true);
		}
	}

}
