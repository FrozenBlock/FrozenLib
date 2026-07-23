package net.frozenblock.lib.block.mixin.piston.shelf;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShelfBlock.class)
public class ShelfBlockMixin {

	@Inject(method = "onPlace", at = @At("HEAD"))
	private void frozenLib$updatePowerOnMovement(
		BlockState state, Level level, BlockPos pos, BlockState replacingState, boolean movedByPiston, CallbackInfo info,
		@Local(argsOnly = true, ordinal = 0) LocalRef<BlockState> stateRef
	) {
		// This is technically unnecessary, but is here just to be safe.
		if (!movedByPiston) return;
		final boolean powered = level.hasNeighborSignal(pos);
		stateRef.set(state.setValue(ShelfBlock.POWERED, powered));
	}
}
