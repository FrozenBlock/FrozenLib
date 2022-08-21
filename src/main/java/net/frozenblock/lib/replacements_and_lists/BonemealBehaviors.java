package net.frozenblock.lib.replacements_and_lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BonemealBehaviors {

    public static Map<Block, BonemealBehavior> bonemeals = new HashMap<>();

    @FunctionalInterface
    public interface BonemealBehavior {
        boolean bonemeal(Level world, BlockPos pos, BlockState state, Direction face, Direction horizontal);
    }

}
