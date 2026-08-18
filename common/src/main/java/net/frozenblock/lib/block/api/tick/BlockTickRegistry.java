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

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentEvents;
import net.frozenblock.lib.block.api.attachment.BlockAttachmentKey;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Lets you add custom behavior to be run upon a block being ticked.
 * <p>
 * If you would like to add custom behavior that runs regardless of the block being ticked, see {@link BlockTickEvents}.
 */
@UtilityClass
public final class BlockTickRegistry {
	private static final BlockAttachmentKey<List<Tick>> TICK_KEY = BlockAttachmentKey.create(() -> "Tick");
	private static final BlockAttachmentKey<List<RandomTick>> RANDOM_TICK_KEY = BlockAttachmentKey.create(() -> "RandomTick");
	private static final BlockAttachmentKey<List<AnimateTick>> ANIMATE_TICK_KEY = BlockAttachmentKey.create(() -> "AnimateTick");

	public static void registerTick(Block block, Tick tick) {
		BlockAttachmentEvents.REGISTER.register(registries -> addTick(block, tick));
	}

	public static void registerTick(TagKey<Block> tag, Tick tick) {
		BlockAttachmentEvents.forAllInTag(tag, (block, registries) -> addTick(block, tick));
	}

	public static void registerRandomTick(Block block, RandomTick randomTick) {
		BlockAttachmentEvents.REGISTER.register(registries -> addRandomTick(block, randomTick));
	}

	public static void registerRandomTick(TagKey<Block> tag, RandomTick randomTick) {
		BlockAttachmentEvents.forAllInTag(tag, (block, registries) -> addRandomTick(block, randomTick));
	}

	public static void registerAnimateTick(Block block, AnimateTick animateTick) {
		BlockAttachmentEvents.REGISTER.register(registries -> addAnimateTick(block, animateTick));
	}

	public static void registerAnimateTick(TagKey<Block> tag, AnimateTick animateTick) {
		BlockAttachmentEvents.forAllInTag(tag, (block, registries) -> addAnimateTick(block, animateTick));
	}

	/**
	 * Runs when {@link BlockState#tick(ServerLevel, BlockPos, RandomSource) tick} is called.
	 */
	@FunctionalInterface
	public interface Tick {
		/**
		 * Runs when {@link BlockState#tick(ServerLevel, BlockPos, RandomSource) tick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link ServerLevel}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the {@link ServerLevel}'s {@link RandomSource}
		 */
		void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
	}

	/**
	 * Runs when {@link BlockState#randomTick(ServerLevel, BlockPos, RandomSource) randomTick} is called.
	 */
	@FunctionalInterface
	public interface RandomTick {
		/**
		 * Runs when {@link BlockState#randomTick(ServerLevel, BlockPos, RandomSource) randomTick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link ServerLevel}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the {@link ServerLevel}'s {@link RandomSource}
		 */
		void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
	}

	/**
	 * Runs when {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource) animateTick} is called.
	 */
	@FunctionalInterface
	public interface AnimateTick {
		/**
		 * Runs when {@link Block#animateTick(BlockState, Level, BlockPos, RandomSource) animateTick} is called.
		 * @param state the {@link BlockState} of the {@link Block}
		 * @param level the current {@link Level}
		 * @param pos the {@link BlockPos} of the {@link Block}
		 * @param random the current {@link RandomSource}
		 */
		void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random);
	}

	@ApiStatus.Internal
	public static void onTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		final List<Tick> ticks = state.getBlock().frozenLib$getAttached(TICK_KEY);
		if (ticks == null) return;
		ticks.forEach(tick -> tick.tick(state, level, pos, random));
	}

	@ApiStatus.Internal
	private static void addTick(Block block, Tick tick) {
		final List<Tick> ticks = block.frozenLib$getAttachedOrDefault(TICK_KEY, new ArrayList<>());
		ticks.add(tick);
		block.frozenLib$setAttached(TICK_KEY, ticks);
	}

	@ApiStatus.Internal
	public static void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		final List<RandomTick> randomTicks = state.getBlock().frozenLib$getAttached(RANDOM_TICK_KEY);
		if (randomTicks == null) return;
		randomTicks.forEach(randomTick -> randomTick.randomTick(state, level, pos, random));
	}

	@ApiStatus.Internal
	private static void addRandomTick(Block block, RandomTick randomTick) {
		final List<RandomTick> randomTicks = block.frozenLib$getAttachedOrDefault(RANDOM_TICK_KEY, new ArrayList<>());
		randomTicks.add(randomTick);
		block.frozenLib$setAttached(RANDOM_TICK_KEY, randomTicks);
	}

	@ApiStatus.Internal
	public static void onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		final List<AnimateTick> animateTicks = state.getBlock().frozenLib$getAttached(ANIMATE_TICK_KEY);
		if (animateTicks == null) return;
		animateTicks.forEach(animateTick -> animateTick.animateTick(state, level, pos, random));
	}

	@ApiStatus.Internal
	private static void addAnimateTick(Block block, AnimateTick animateTick) {
		final List<AnimateTick> animateTicks = block.frozenLib$getAttachedOrDefault(ANIMATE_TICK_KEY, new ArrayList<>());
		animateTicks.add(animateTick);
		block.frozenLib$setAttached(ANIMATE_TICK_KEY, animateTicks);
	}
}
