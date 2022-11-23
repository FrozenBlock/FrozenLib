/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.mixin.server;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.impl.DripstoneDripLavaFrom;
import net.frozenblock.lib.impl.DripstoneDripWaterFrom;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

	@Final
	@Shadow
	private static VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK;

	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = {"m_ulptarvl", "method_33279", "lambda$getFluidAboveStalactite$11"}, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true, require = 1)
	private static void getFluidAboveStalactite(Level level, BlockPos pos, CallbackInfoReturnable<PointedDripstoneBlock.FluidInfo> cir, BlockPos blockPos, BlockState blockState) {
		if (!FrozenBools.useNewDripstoneLiquid && blockPos != null) {
			if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock()) && !level.dimensionType().ultraWarm()) {
				cir.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.WATER, blockState));
			} else if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(blockState.getBlock())) {
				cir.setReturnValue(new PointedDripstoneBlock.FluidInfo(blockPos, Fluids.LAVA, blockState));
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

	@Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	private static void maybeTransferFluid(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, Optional<PointedDripstoneBlock.FluidInfo> optional, Fluid fluid, float f, BlockPos blockPos) {
		if (optional.isPresent()) {
			PointedDripstoneBlock.FluidInfo fluidInfo = optional.get();
			Block block = optional.get().sourceState().getBlock();
			if (DripstoneDripWaterFrom.ON_DRIP_BLOCK.containsKey(block) && fluid == Fluids.WATER) {
				DripstoneDripWaterFrom.ON_DRIP_BLOCK.get(block).drip(level, fluidInfo, blockPos);
				ci.cancel();
			}
			if (DripstoneDripLavaFrom.ON_DRIP_BLOCK.containsKey(block) && fluid == Fluids.LAVA) {
				DripstoneDripLavaFrom.ON_DRIP_BLOCK.get(block).drip(level, fluidInfo, blockPos);
				ci.cancel();
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "findFillableCauldronBelowStalactiteTip", cancellable = true)
	private static void findFillableCauldronBelowStalactiteTip(Level world, BlockPos pos2, Fluid fluid, CallbackInfoReturnable<BlockPos> info) {
		Predicate<BlockState> tagPredicate = state -> state.is(FrozenBlockTags.DRIPSTONE_CAN_DRIP_ON);
		if (tagPredicate.test(world.getBlockState(pos2.mutable().move(Direction.get(Direction.DOWN.getAxisDirection(), Direction.Axis.Y))))) {
			Predicate<BlockState> predicate = tagPredicate.or(state -> (state.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock) state.getBlock()).canReceiveStalactiteDrip(fluid)));
			BiPredicate<BlockPos, BlockState> biPredicate = (pos, state) -> canDripThrough(world, pos, state);
			info.setReturnValue(findBlockVertical(world, pos2, Direction.DOWN.getAxisDirection(), biPredicate, predicate, 11).orElse(null));
			info.cancel();
		}

	}

	@Shadow
	private static boolean canDripThrough(BlockGetter world, BlockPos pos, BlockState state) {
		if (state.isAir()) {
			return true;
		}
		if (state.isSolidRender(world, pos)) {
			return false;
		}
		if (!state.getFluidState().isEmpty()) {
			return false;
		}
		VoxelShape voxelShape = state.getCollisionShape(world, pos);
		return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, voxelShape, BooleanOp.AND);
	}

	@Shadow
	private static Optional<BlockPos> findBlockVertical(LevelAccessor world, BlockPos pos, Direction.AxisDirection direction, BiPredicate<BlockPos, BlockState> continuePredicate, Predicate<BlockState> stopPredicate, int range) {
		Direction direction2 = Direction.get(direction, Direction.Axis.Y);
		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (int i = 1; i < range; ++i) {
			mutable.move(direction2);
			BlockState blockState = world.getBlockState(mutable);
			if (stopPredicate.test(blockState)) {
				return Optional.of(mutable.immutable());
			}
			if (!world.isOutsideBuildHeight(mutable.getY()) && continuePredicate.test(mutable, blockState)) continue;
			return Optional.empty();
		}
		return Optional.empty();
	}

	@Shadow
	private static boolean isStalactite(BlockState state) {
		return isPointedDripstoneWithDirection(state, Direction.DOWN);
	}

	@Shadow
	private static boolean isStalagmite(BlockState state) {
		return isPointedDripstoneWithDirection(state, Direction.UP);
	}

	@Shadow
	private static boolean isPointedDripstoneWithDirection(BlockState state, Direction dir) {
		return state.is(Blocks.POINTED_DRIPSTONE) && state.getValue(BlockStateProperties.VERTICAL_DIRECTION) == dir;
	}

	@Shadow
	private static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState state, int maxIterations) {
		return Optional.empty();
	}

}
