/*
 * Copyright (C) 2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.api.tick;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Lets you add custom behavior to be run upon a block being ticked.
 */
public class BlockScheduledTicks {
	@ApiStatus.Internal
	private static final Map<Block, List<InjectedScheduledTick>> TICKS = new Object2ObjectOpenHashMap<>();

	/**
	 * Adds custom tick behavior to a {@link Block}.
	 *
	 * @param block                 The {@link Block} to add custom tick behavior to.
	 * @param injectedScheduledTick The behavior to run upon the {@link Block} being ticked.
	 */
	public static void addToBlock(Block block, InjectedScheduledTick injectedScheduledTick) {
		if (TICKS.containsKey(block)) {
			TICKS.get(block).add(injectedScheduledTick);
		} else {
			TICKS.put(block, Lists.newArrayList(injectedScheduledTick));
		}
	}

	@ApiStatus.Internal
	public static void runTickIfPresent(@NotNull BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		Block block = state.getBlock();
		if (TICKS.containsKey(block)) {
			TICKS.get(block).forEach(injectedScheduledTick -> injectedScheduledTick.tick(state, world, pos, random));
		}
	}

	@FunctionalInterface
	public interface InjectedScheduledTick {
		void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random);
	}
}
