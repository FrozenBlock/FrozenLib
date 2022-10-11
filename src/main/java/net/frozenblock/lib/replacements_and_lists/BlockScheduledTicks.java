package net.frozenblock.lib.replacements_and_lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BlockScheduledTicks {

    public static Map<Block, InjectedScheduledTick> ticks = new HashMap<>();

    @FunctionalInterface
    public interface InjectedScheduledTick {
        void tick(BlockState state, ServerLevel world, BlockPos pos,
                  RandomSource random);
    }

}
