package net.frozenblock.lib.replacements_and_lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;

import java.util.HashMap;
import java.util.Map;

public class DripstoneDripWaterFrom {

    public static Map<Block, InjectedOnDrip> map = new HashMap<>();

    @FunctionalInterface
    public interface InjectedOnDrip {
        void drip(ServerLevel world, PointedDripstoneBlock.FluidInfo fluidInfo,
                  BlockPos pos);
    }

}
