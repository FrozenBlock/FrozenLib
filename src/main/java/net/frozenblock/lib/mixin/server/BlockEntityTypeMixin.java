package net.frozenblock.lib.mixin.server;

import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

	/**
	 * This allows custom sign and hanging sign blocks to be added to their block entities
	 */
	@Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
	private void isValid(BlockState state, CallbackInfoReturnable<Boolean> info) {
		var type = BlockEntityType.class.cast(this);

		if ((type == BlockEntityType.SIGN &&
				(state.getBlock() instanceof StandingSignBlock || state.getBlock() instanceof WallSignBlock))
				|| (type == BlockEntityType.HANGING_SIGN
				&& (state.getBlock() instanceof CeilingHangingSignBlock || state.getBlock() instanceof WallHangingSignBlock))) {
			info.setReturnValue(true);
		}
	}
}
