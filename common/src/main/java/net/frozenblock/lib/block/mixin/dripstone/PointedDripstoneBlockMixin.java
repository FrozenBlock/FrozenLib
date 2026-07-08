/*
 * Copyright (C) 2025-2026 FrozenBlock
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
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
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
		method = "lambda$getFluidAboveStalactite$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
		),
		cancellable = true
	)
	private static void frozenLib$getFluidAboveStalactite(
		Level level, BlockPos rootPos, CallbackInfoReturnable<PointedDripstoneBlock.FluidInfo> info,
		@Local(name = "abovePos") BlockPos abovePos, @Local(name = "aboveState") BlockState aboveState
	) {
		if (abovePos == null || aboveState == null) return;
		if (DripstoneDripApi.containsWaterDrip(aboveState.getBlock()) && !level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, abovePos)) {
			info.setReturnValue(new PointedDripstoneBlock.FluidInfo(abovePos, Fluids.WATER, aboveState));
		} else if (DripstoneDripApi.containsLavaDrip(aboveState.getBlock())) {
			info.setReturnValue(new PointedDripstoneBlock.FluidInfo(abovePos, Fluids.LAVA, aboveState));
		}
	}

	@Inject(
		method = "maybeTransferFluid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
		),
		cancellable = true
	)
	private static void frozenLib$maybeTransferFluid(
		BlockState state, ServerLevel level, BlockPos pos, float randomValue, CallbackInfo info,
		@Local(name = "fluidInfo") Optional<PointedDripstoneBlock.FluidInfo> fluidInfo,
		@Local(name = "fluid") Fluid fluid,
		@Local(name = "stalactiteTipPos") BlockPos stalactiteTipPos
	) {
		if (fluidInfo.isEmpty()) return;

		final Block fluidBlock = fluidInfo.get().sourceState().getBlock();
		if (DripstoneDripApi.containsWaterDrip(fluidBlock) && fluid == Fluids.WATER) {
			DripstoneDripApi.runWaterDripsIfPresent(fluidBlock, level, stalactiteTipPos, fluidInfo.get());
			info.cancel();
		} else if (DripstoneDripApi.containsLavaDrip(fluidBlock) && fluid == Fluids.LAVA) {
			DripstoneDripApi.runLavaDripsIfPresent(fluidBlock, level, stalactiteTipPos, fluidInfo.get());
			info.cancel();
		}
	}

	@Inject(method = "lambda$findFillableCauldronBelowStalactiteTip$0", at = @At(value = "HEAD"), cancellable = true)
	private static void frozenLib$dripOnNewAllowedBlocks(Fluid fluid, BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (state.is(FrozenLibBlockTags.DRIPSTONE_CAN_DRIP_ON)) info.setReturnValue(true);
	}
}
