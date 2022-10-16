package net.frozenblock.lib.mixin.server;

import net.frozenblock.lib.replacements_and_lists.BlockScheduledTicks;
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
    public void tickScheduled(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo info) {
        if (BlockScheduledTicks.ticks.containsKey(state.getBlock())) {
            BlockScheduledTicks.ticks.get(state.getBlock()).tick(state, world, pos, random);
        }
    }

}
