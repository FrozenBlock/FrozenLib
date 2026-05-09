package net.frozenblock.lib.block.mixin.waterlike;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class LevelMixin {

	@WrapOperation(
		method = "removeBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
		)
	)
	public FluidState frozenLib$preventWaterLikeFromMakingWaterOnRemove(Level instance, BlockPos pos, Operation<FluidState> original) {
		final FluidState fluidState = original.call(instance, pos);
		if (fluidState.is(Fluids.WATER) && instance.getBlockState(pos).getBlock() instanceof WaterLikeBlock) return Fluids.EMPTY.defaultFluidState();
		return fluidState;
	}

	@ModifyExpressionValue(
		method = "destroyBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
		)
	)
	public FluidState frozenLib$preventWaterLikeBlockFromMakingWaterOnDestroy(
		FluidState fluidState,
		@Local(name = "blockState") BlockState blockState
	) {
		if (blockState.getBlock() instanceof WaterLikeBlock) return Fluids.EMPTY.defaultFluidState();
		return fluidState;
	}

}
