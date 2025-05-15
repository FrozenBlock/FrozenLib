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

package net.frozenblock.lib.worldgen.feature.api.block_predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatureUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SearchInAreaBlockPredicate implements BlockPredicate {
	public static final MapCodec<SearchInAreaBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			BlockPredicate.CODEC.fieldOf("block_predicate").forGetter(config -> config.blockPredicate),
			Codec.INT.fieldOf("search_area").forGetter(config -> config.searchArea)
		).apply(instance, SearchInAreaBlockPredicate::new)
	);

	private final BlockPredicate blockPredicate;
	private final int searchArea;

	public SearchInAreaBlockPredicate(
		@NotNull BlockPredicate blockPredicate,
		int searchArea
	) {
		this.blockPredicate = blockPredicate;
		this.searchArea = searchArea;
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasAirWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE, searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasAirOrWaterWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE, searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasAirOrLavaWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.LAVA), searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasAirOrWaterOrLavaWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.LAVA), searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasWaterWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER), searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasLavaWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.matchesBlocks(Blocks.LAVA), searchArea);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SearchInAreaBlockPredicate hasWaterOrLavaWithin(int searchArea) {
		return new SearchInAreaBlockPredicate(BlockPredicate.matchesBlocks(Blocks.WATER, Blocks.LAVA), searchArea);
	}

	@Override
	public @NotNull BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.SEARCH_IN_AREA;
	}

	@Override
	public boolean test(WorldGenLevel level, @NotNull BlockPos pos) {
		return FrozenLibFeatureUtils.matchesConditionNearby(level, pos, this.searchArea, this.blockPredicate);
	}

}
