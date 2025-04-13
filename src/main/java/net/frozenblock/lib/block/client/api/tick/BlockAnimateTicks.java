/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.block.client.api.tick;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Lets you add custom behavior to be run each time {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource)} is called.
 */
@Environment(EnvType.CLIENT)
public class BlockAnimateTicks {
	@ApiStatus.Internal
	private static final List<Pair<Predicate<BlockState>, InjectedAnimateTick>> ANIMATE_TICKS = new ArrayList<>();

	public static void addForBlockState(BlockState state, InjectedAnimateTick animateTick) {
		add(blockState -> blockState == state, animateTick);
	}

	public static void addForBlock(Block block, InjectedAnimateTick animateTick) {
		add(blockState -> blockState.is(block), animateTick);
	}

	public static void addForBlockTag(TagKey<Block> tagKey, InjectedAnimateTick animateTick) {
		add(blockState -> blockState.is(tagKey), animateTick);
	}

	public static void addForBlockClass(Class<? extends Block> blockClass, InjectedAnimateTick animateTick) {
		add(blockState -> blockState.getBlock().getClass().isInstance(blockClass), animateTick);
	}

	public static void addForExactBlockClass(Class<? extends Block> blockClass, InjectedAnimateTick animateTick) {
		add(blockState -> blockState.getBlock().getClass().equals(blockClass), animateTick);
	}

	public static void add(Predicate<BlockState> predicate, InjectedAnimateTick animateTick) {
		ANIMATE_TICKS.add(Pair.of(predicate, animateTick));
	}

	@ApiStatus.Internal
	public static void onAnimateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
		ANIMATE_TICKS.forEach(pair -> {
			if (pair.getFirst().test(blockState)) {
				pair.getSecond().animateTick(blockState, level, blockPos, randomSource);
			}
		});
	}

	@FunctionalInterface
	public interface InjectedAnimateTick {
		void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource);
	}
}
