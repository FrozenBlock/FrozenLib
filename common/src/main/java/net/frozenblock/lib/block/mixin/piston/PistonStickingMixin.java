package net.frozenblock.lib.block.mixin.piston;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.impl.piston.PistonPushUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonStructureResolver.class)
public class PistonStickingMixin {

	@Shadow
	@Final
	private Direction pushDirection;

	@WrapOperation(
		method = {"resolve", "addBlockLine"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean frozenLib$determineIsSticky(BlockState state, Operation<Boolean> original) {
		return PistonPushUtil.isSticky(original.call(state), state, this.pushDirection);
	}

	@WrapOperation(
		method = "addBlockLine",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean frozenLib$addBlockLineBlockSticking(
		BlockState state1, BlockState state2, Operation<Boolean> original
	) {
		return PistonPushUtil.canBlocksStickTogether(original.call(state1, state2), state1, state2, this.pushDirection.getOpposite());
	}

	@WrapOperation(
		method = "addBranchingBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean frozenLib$addBranchingBlocksShelfSticking(
		BlockState state1, BlockState state2, Operation<Boolean> original,
		@Local(name = "direction") Direction direction
	) {
		return PistonPushUtil.canBlocksStickTogether(original.call(state1, state2), state2, state1, direction);
	}
}
