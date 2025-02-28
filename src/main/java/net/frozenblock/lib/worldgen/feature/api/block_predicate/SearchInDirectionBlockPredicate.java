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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.NotNull;

public class SearchInDirectionBlockPredicate implements BlockPredicate {
	public static final MapCodec<SearchInDirectionBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			BlockPredicate.CODEC
				.fieldOf("block_predicate")
				.forGetter(config -> config.blockPredicate),
			Codec.INT
				.fieldOf("search_steps")
				.forGetter(config -> config.searchSteps),
			Direction.CODEC
				.fieldOf("search_direction")
				.forGetter(config -> config.searchDirection),
			Codec.BOOL
				.fieldOf("all_must_match")
				.forGetter(config -> config.allMustMatch)
		).apply(instance, SearchInDirectionBlockPredicate::new)
	);

	private final BlockPredicate blockPredicate;
	private final int searchSteps;
	private final Direction searchDirection;
	private final boolean allMustMatch;

	public SearchInDirectionBlockPredicate(
		@NotNull BlockPredicate blockPredicate,
		int searchSteps,
		@NotNull Direction searchDirection,
		boolean allMustMatch
	) {
		this.blockPredicate = blockPredicate;
		this.searchSteps = searchSteps;
		this.searchDirection = searchDirection;
		this.allMustMatch = allMustMatch;
	}

	public static @NotNull SearchInDirectionBlockPredicate anyAboveMatch(BlockPredicate blockPredicate, int searchSteps) {
		return new SearchInDirectionBlockPredicate(blockPredicate, searchSteps, Direction.UP, false);
	}

	public static @NotNull SearchInDirectionBlockPredicate allAboveMatch(BlockPredicate blockPredicate, int searchSteps) {
		return new SearchInDirectionBlockPredicate(blockPredicate, searchSteps, Direction.UP, true);
	}

	public static @NotNull SearchInDirectionBlockPredicate anyBelowMatch(BlockPredicate blockPredicate, int searchSteps) {
		return new SearchInDirectionBlockPredicate(blockPredicate, searchSteps, Direction.UP, false);
	}

	public static @NotNull SearchInDirectionBlockPredicate allBelowMatch(BlockPredicate blockPredicate, int searchSteps) {
		return new SearchInDirectionBlockPredicate(blockPredicate, searchSteps, Direction.UP, true);
	}

	@Override
	public @NotNull BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.SEARCH_IN_DIRECTION;
	}

	@Override
	public boolean test(WorldGenLevel level, @NotNull BlockPos pos) {
		BlockPos.MutableBlockPos mutablePos = pos.mutable();
		for (int step = 1; step <= this.searchSteps; step++) {
			if (this.blockPredicate.test(level, mutablePos.move(this.searchDirection, step))) {
				if (!this.allMustMatch) return true;
			} else if (this.allMustMatch) {
				return false;
			}
		}
		return this.allMustMatch;
	}

}
