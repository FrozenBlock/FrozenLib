package net.frozenblock.lib.block.mixin.waterlike;

import net.frozenblock.lib.block.api.waterlike.WaterLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

	@Shadow
	protected abstract BlockState asState();

	@Inject(at = @At("HEAD"), method = "handleNeighborChanged")
	public void frozenLib$neighborChanged(Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston, CallbackInfo info) {
		if (level.isClientSide()) return;
		if (!(this.asState().getBlock() instanceof BubbleColumnBlock bubbleColumn) || !WaterLikeBlock.supportsBubbleColumns(this.asState())) return;
		level.scheduleTick(pos, bubbleColumn, BubbleColumnBlock.CHECK_PERIOD);
	}

}
