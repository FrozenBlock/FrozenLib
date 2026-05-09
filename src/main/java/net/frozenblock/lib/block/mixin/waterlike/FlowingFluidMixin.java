package net.frozenblock.lib.block.mixin.waterlike;

import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {

	@Inject(method = "spread", at = @At("HEAD"), cancellable = true)
	public void frozenLib$stopWaterLikeFluidSpread(ServerLevel level, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfo info) {
		if (state.getBlock() instanceof WaterLikeBlock) info.cancel();
	}

}
