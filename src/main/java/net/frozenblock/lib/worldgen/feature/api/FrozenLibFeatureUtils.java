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

package net.frozenblock.lib.worldgen.feature.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class FrozenLibFeatureUtils {
	public static BootstrapContext<Object> BOOTSTRAP_CONTEXT = null;

	public static boolean isBlockExposed(WorldGenLevel level, @NotNull BlockPos blockPos) {
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		for (Direction direction : Direction.values()) {
			BlockState blockState = level.getBlockState(mutableBlockPos.setWithOffset(blockPos, direction));
			if (blockState.canBeReplaced()) return true;
		}
		return false;
	}

	public static boolean matchesConditionsTouching(WorldGenLevel level, @NotNull BlockPos blockPos, boolean requiredOnAllSides, BlockPredicate predicate) {
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		int validSides = 0;

		for (Direction direction : Direction.values()) {
			if (predicate.test(level, mutableBlockPos.setWithOffset(blockPos, direction))) {
				if (!requiredOnAllSides) return true;
				validSides += 1;
			}
		}
		return validSides == 6;
	}

	public static boolean isAirOrWaterNearby(WorldGenLevel level, @NotNull BlockPos pos, int searchDistance) {
		return matchesConditionNearby(level, pos, searchDistance, BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE);
	}

	public static boolean isWaterNearby(WorldGenLevel level, @NotNull BlockPos pos, int searchDistance) {
		return matchesConditionNearby(level, pos, searchDistance, BlockPredicate.matchesBlocks(Blocks.WATER));
	}

	public static boolean matchesConditionNearby(WorldGenLevel level, @NotNull BlockPos pos, int searchDistance, BlockPredicate predicate) {
		Iterable<BlockPos> poses = BlockPos.betweenClosed(
			pos.offset(-searchDistance, -searchDistance, -searchDistance),
			pos.offset(searchDistance, searchDistance, searchDistance)
		);
		for (BlockPos currentPos : poses) if (predicate.test(level, currentPos)) return true;
		return false;
	}
}
