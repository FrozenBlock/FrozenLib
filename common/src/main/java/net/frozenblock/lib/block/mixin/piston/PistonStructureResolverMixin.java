package net.frozenblock.lib.block.mixin.piston;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.block.api.piston.PistonEvents;
import net.frozenblock.lib.block.impl.piston.PistonPushUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {

	@Shadow
	@Final
	private Direction pushDirection;

	@Shadow
	@Final
	private Level level;

	@WrapOperation(
		method = "resolve",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;addBlockLine(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
		)
	)
	public boolean frozenLib$onPushFail(PistonStructureResolver instance, BlockPos start, Direction direction, Operation<Boolean> original) {
		final boolean resolved = original.call(instance, start, direction);
		if (!resolved) PistonEvents.ON_PUSH_FAIL.invoker().onPushFail(this.level, start, this.level.getBlockState(start), direction);
		return resolved;
	}

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
