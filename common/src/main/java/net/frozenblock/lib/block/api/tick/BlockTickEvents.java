/*
 * Copyright (C) 2025-2026 FrozenBlock
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

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lets you add custom behavior to be run upon a block being ticked.
 */
@UtilityClass
public final class BlockTickEvents {
	/**
	 * The event that is triggered when {@link BlockState#tick(ServerLevel, BlockPos, RandomSource) tick} is called.
	 */
	public static final Event<Tick> TICK = EventRegistry.createEnvironmentEvent(Tick.class,
		callbacks -> (state, level, pos, random) -> {
		for (var callback : callbacks) callback.onTick(state, level, pos, random);
	});

	/**
	 * The event that is triggered when {@link BlockState#randomTick(ServerLevel, BlockPos, RandomSource) randomTick} is called.
	 */
	public static final Event<RandomTick> RANDOM_TICK = EventRegistry.createEnvironmentEvent(RandomTick.class,
		callbacks -> (state, level, pos, random) -> {
		for (var callback : callbacks) callback.onRandomTick(state, level, pos, random);
	});

	/**
	 * The event that is triggered when {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource) animateTick} is called.
	 */
	public static final Event<AnimateTick> ANIMATE_TICK = EventRegistry.createEnvironmentEvent(AnimateTick.class,
		callbacks -> (state, level, pos, random) -> {
		for (var callback : callbacks) callback.onAnimateTick(state, level, pos, random);
	});

	/**
	 * A functional interface representing a Tick event.
	 */
	@FunctionalInterface
	public interface Tick extends CommonEventEntrypoint {
		/**
		 * Runs when {@link BlockState#tick(ServerLevel, BlockPos, RandomSource) tick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link ServerLevel}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the {@link ServerLevel}'s {@link RandomSource}
		 */
		void onTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
	}

	/**
	 * A functional interface representing a Random Tick event.
	 */
	@FunctionalInterface
	public interface RandomTick extends CommonEventEntrypoint {
		/**
		 * Runs when {@link BlockState#randomTick(ServerLevel, BlockPos, RandomSource) randomTick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link ServerLevel}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the {@link ServerLevel}'s {@link RandomSource}
		 */
		void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
	}

	/**
	 * A functional interface representing an Animate Tick event.
	 */
	@FunctionalInterface
	public interface AnimateTick extends CommonEventEntrypoint {
		/**
		 * Runs when {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource) animateTick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link Level}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the current {@link RandomSource}
		 */
		void onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random);
	}
}
