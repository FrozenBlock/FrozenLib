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

package net.frozenblock.lib.block.mixin.dripstone;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.block.api.dripstone.DripstoneDripLavaFrom;
import net.frozenblock.lib.block.api.dripstone.DripstoneDripWaterFrom;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
		method = {"m_ulptarvl", "method_33279", "lambda$getFluidAboveStalactite$11"},
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z",
			shift = At.Shift.BEFORE
		),
		cancellable = true,
		require = 1
	)
    private static void frozenLib$getFluidAboveStalactite(
		Level level, BlockPos pos, CallbackInfoReturnable<PointedDripstoneBlock.FluidInfo> info,
		@Local(ordinal = 1) BlockPos blockPos, @Local BlockState blockState
	) {
        if (!FrozenBools.useNewDripstoneLiquid && blockPos != null) {
            if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock()) && !level.dimensionType().ultraWarm()) {
                info.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.WATER, blockState));
            } else if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock())) {
                info.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.LAVA, blockState));
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getFluidAboveStalactite", cancellable = true)
    private static void getFluidAboveStalactite(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Optional<PointedDripstoneBlock.FluidInfo>> info) {
        if (FrozenBools.useNewDripstoneLiquid) {
            info.setReturnValue(
                !isStalactite(state) ? Optional.empty() : findRootBlock(level, pos, state, 11).map((posx) -> {

                    BlockState firstState = level.getBlockState(posx);
                    if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(firstState.getBlock()) && !level.dimensionType().ultraWarm()) {
                        return new PointedDripstoneBlock.FluidInfo(posx, Fluids.WATER, firstState);
                    } else if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(firstState.getBlock())) {
                        return new PointedDripstoneBlock.FluidInfo(posx, Fluids.LAVA, firstState);
                    }
                    BlockPos blockPos = posx.above();
                    BlockState blockState = level.getBlockState(blockPos);
                    Fluid fluid;
                    if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock()) && !level.dimensionType().ultraWarm()) {
                        return new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.WATER, blockState);
                    } else if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock())) {
                        return new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.LAVA, blockState);
                    } else {
                        fluid = level.getFluidState(blockPos).getType();
                    }

                    return new PointedDripstoneBlock.FluidInfo(blockPos, fluid, blockState);
                })
            );
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
            if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(block) && fluid == Fluids.WATER) {
                DripstoneDripWaterFrom.ON_DRIP_BLOCK.get(block).drip(level, fluidInfo, blockPos);
				info.cancel();
            }
            if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(block) && fluid == Fluids.LAVA) {
                DripstoneDripLavaFrom.ON_DRIP_BLOCK.get(block).drip(level, fluidInfo, blockPos);
				info.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "findFillableCauldronBelowStalactiteTip", cancellable = true)
    private static void frozenLib$findFillableCauldronBelowStalactiteTip(Level world, BlockPos pos2, Fluid fluid, CallbackInfoReturnable<BlockPos> info) {
        Predicate<BlockState> tagPredicate = state -> state.is(FrozenBlockTags.DRIPSTONE_CAN_DRIP_ON);
        if (tagPredicate.test(world.getBlockState(pos2.mutable().move(Direction.get(Direction.DOWN.getAxisDirection(), Direction.Axis.Y))))) {
            Predicate<BlockState> predicate = tagPredicate.or(state -> (state.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock) state.getBlock()).canReceiveStalactiteDrip(fluid)));
            BiPredicate<BlockPos, BlockState> biPredicate = (pos, state) -> canDripThrough(world, pos, state);
            info.setReturnValue(findBlockVertical(world, pos2, Direction.DOWN.getAxisDirection(), biPredicate, predicate, 11).orElse(null));
        }

    }

    @Shadow
    private static boolean canDripThrough(BlockGetter world, BlockPos pos, BlockState state) {
        throw new AssertionError("Mixin injection failed - FrozenLib PointedDripstoneBlockMixin.");
    }

    @Shadow
    private static Optional<BlockPos> findBlockVertical(LevelAccessor world, BlockPos pos, Direction.AxisDirection direction, BiPredicate<BlockPos, BlockState> continuePredicate, Predicate<BlockState> stopPredicate, int range) {
		throw new AssertionError("Mixin injection failed - FrozenLib PointedDripstoneBlockMixin.");
    }

    @Shadow
    private static boolean isStalactite(BlockState state) {
		throw new AssertionError("Mixin injection failed - FrozenLib PointedDripstoneBlockMixin.");
    }

    @Shadow
    private static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState state, int maxIterations) {
		throw new AssertionError("Mixin injection failed - FrozenLib PointedDripstoneBlockMixin.");
    }

}
