package net.frozenblock.lib.shovel.impl;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelBehaviors {

	public static final Map<Block, ShovelBehavior> SHOVEL_BEHAVIORS = new HashMap<>();

	@FunctionalInterface
	public interface ShovelBehavior {
		boolean shovel(UseOnContext context, Level world, BlockPos pos, BlockState state, Direction face, Direction horizontal);
	}

}
