/*
 * Copyright 2023-2025 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.api.block_predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatureUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

public class SearchingBlockPredicate implements BlockPredicate {
	public static final MapCodec<SearchingBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			BlockPredicate.CODEC
				.fieldOf("block_predicate")
				.forGetter(config -> config.blockPredicate),
			SearchType.CODEC
				.fieldOf("search_type")
				.forGetter(config -> config.searchType),
			Codec.BOOL
				.lenientOptionalFieldOf("invert_search_condition", false)
				.forGetter(config -> config.invertSearchCondition)
		).apply(instance, SearchingBlockPredicate::new)
	);

	private final BlockPredicate blockPredicate;
	private final SearchType searchType;
	private final boolean invertSearchCondition;

	public SearchingBlockPredicate(
		@NotNull BlockPredicate blockPredicate,
		SearchType searchType,
		boolean invertSearchCondition
	) {
		this.blockPredicate = blockPredicate;
		this.searchType = searchType;
		this.invertSearchCondition = invertSearchCondition;
	}

	@Override
	public @NotNull BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.SEARCH;
	}

	@Override
	public boolean test(WorldGenLevel level, @NotNull BlockPos pos) {
		return this.invertSearchCondition != this.searchType.isConditionMet(level, pos.mutable(), this.blockPredicate);
	}

	public enum SearchType implements StringRepresentable {
		ALWAYS_TRUE("always_true", (level, pos, searchCondition) -> true),

		TOUCHING("touching", (level, pos, searchCondition) -> {
			return FrozenLibFeatureUtils.matchesConditionsTouching(level, pos, false, searchCondition);
		}),
		ALL_TOUCHING("all_touching", (level, pos, searchCondition) -> {
			return FrozenLibFeatureUtils.matchesConditionsTouching(level, pos, true, searchCondition);
		}),

		BELOW("below", (level, pos, searchCondition) -> {
			return searchCondition.test(level, pos.move(Direction.UP));
		}),
		BELOW_TWO("below_within_two_blocks", (level, pos, searchCondition) -> {
			return BELOW.isConditionMet(level, pos, searchCondition) || searchCondition.test(level, pos.move(Direction.UP));
		}),
		BELOW_THREE("below_within_three_blocks", (level, pos, searchCondition) -> {
			return BELOW_TWO.isConditionMet(level, pos, searchCondition) || searchCondition.test(level, pos.move(Direction.UP));
		}),

		ABOVE("above", (level, pos, searchCondition) -> {
			return searchCondition.test(level, pos.move(Direction.DOWN));
		}),
		ABOVE_TWO("above_within_two_blocks", (level, pos, searchCondition) -> {
			return ABOVE.isConditionMet(level, pos, searchCondition) || searchCondition.test(level, pos.move(Direction.DOWN));
		}),
		ABOVE_THREE("above_within_three_blocks", (level, pos, searchCondition) -> {
			return ABOVE_TWO.isConditionMet(level, pos, searchCondition) || searchCondition.test(level, pos.move(Direction.DOWN));
		}),

		NEAR_WITHIN_ONE_BLOCK("near_within_one_block", ((level, pos, searchCondition) -> {
			return FrozenLibFeatureUtils.matchesConditionNearby(level, pos, 1, searchCondition);
		})),
		NEAR_WITHIN_TWO_BLOCKS("near_within_two_blocks", ((level, pos, searchCondition) -> {
			return FrozenLibFeatureUtils.matchesConditionNearby(level, pos, 2, searchCondition);
		})),
		NEAR_WITHIN_THREE_BLOCKS("near_within_three_blocks", ((level, pos, searchCondition) -> {
			return FrozenLibFeatureUtils.matchesConditionNearby(level, pos, 3, searchCondition);
		}));
		public static final Codec<SearchType> CODEC = StringRepresentable.fromEnum(SearchType::values);

		private final String name;
		private final SearchOperation searchOperation;

		SearchType(String name, SearchOperation searchOperation) {
			this.name = name;
			this.searchOperation = searchOperation;
		}

		public boolean isConditionMet(WorldGenLevel level, BlockPos.MutableBlockPos pos, BlockPredicate blockPredicate) {
			return this.searchOperation.test(level, pos, blockPredicate);
		}

		@Override
		public @NotNull String getSerializedName() {
			return this.name;
		}
	}

	@FunctionalInterface
	private interface SearchOperation {
		boolean test(WorldGenLevel level, BlockPos.MutableBlockPos pos, BlockPredicate blockPredicate);
	}

	public static class Builder {
		private BlockPredicate blockPredicate = BlockPredicate.replaceable();
		private final SearchType searchType;
		private boolean invertSearchCondition = false;

		public Builder(SearchType searchType) {
			this.searchType = searchType;
		}

		@Contract(" -> new")
		public static @NotNull Builder exposed() {
			return new Builder(SearchType.TOUCHING);
		}

		public static Builder exposedToWater() {
			return exposed()
				.blockPredicate(blockPredicate -> BlockPredicate.allOf(
					BlockPredicate.matchesBlocks(Blocks.WATER),
					blockPredicate
				));
		}

		public static Builder belowWater() {
			return new Builder(SearchType.BELOW)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER));
		}

		public static Builder belowWaterWithinTwoBlocks() {
			return new Builder(SearchType.BELOW_TWO)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER));
		}

		public static Builder belowWaterWithinThreeBlocks() {
			return new Builder(SearchType.BELOW_THREE)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER));
		}

		public static Builder hasAirOrWaterWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
		}

		public static Builder hasAirOrWaterWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
		}

		public static Builder hasAirOrWaterWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
		}

		public static Builder hasWaterWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER));
		}

		public static Builder hasWaterWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
		}

		public static Builder hasWaterWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER));
		}

		public static Builder exposedToLava() {
			return new Builder(SearchType.TOUCHING)
				.blockPredicate(blockPredicate -> BlockPredicate.allOf(
					BlockPredicate.matchesBlocks(Blocks.LAVA),
					blockPredicate
				));
		}

		public static Builder belowLava() {
			return new Builder(SearchType.BELOW)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder belowLavaWithinTwoBlocks() {
			return new Builder(SearchType.BELOW_TWO)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder belowLavaWithinThreeBlocks() {
			return new Builder(SearchType.BELOW_THREE)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder hasAirOrLavaWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.LAVA));
		}

		public static Builder hasAirOrLavaWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.LAVA));
		}

		public static Builder hasAirOrLavaWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.LAVA));
		}

		public static Builder hasLavaWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder hasLavaWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder hasLavaWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA));
		}

		public static Builder hasAirOrFluidWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.LAVA));
		}

		public static Builder hasAirOrFluidWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.LAVA));
		}

		public static Builder hasAirOrFluidWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.LAVA));
		}

		public static Builder hasAirWithinOneBlock() {
			return new Builder(SearchType.NEAR_WITHIN_ONE_BLOCK)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
		}

		public static Builder hasAirWithinTwoBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_TWO_BLOCKS)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
		}

		public static Builder hasAirWithinThreeBlocks() {
			return new Builder(SearchType.NEAR_WITHIN_THREE_BLOCKS)
				.blockPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
		}

		public Builder blockPredicate(BlockPredicate blockPredicate) {
			this.blockPredicate = blockPredicate;
			return this;
		}

		public Builder blockPredicate(@NotNull Function<BlockPredicate, BlockPredicate> blockPredicate) {
			this.blockPredicate = blockPredicate.apply(this.blockPredicate);
			return this;
		}

		public Builder invertSearchCondition() {
			this.invertSearchCondition = true;
			return this;
		}

		public SearchingBlockPredicate build() {
			return new SearchingBlockPredicate(
				this.blockPredicate,
				this.searchType,
				this.invertSearchCondition
			);
		}
	}
}
