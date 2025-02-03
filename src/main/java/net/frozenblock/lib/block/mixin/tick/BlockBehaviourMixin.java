package net.frozenblock.lib.block.mixin.tick;

import net.frozenblock.lib.block.api.tick.BlockScheduledTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	public void frozenLib$runCustomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo info) {
		BlockScheduledTicks.runTickIfPresent(state, world, pos, random);
	}

}
