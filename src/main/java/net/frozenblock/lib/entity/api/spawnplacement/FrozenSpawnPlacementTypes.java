/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
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

package net.frozenblock.lib.entity.api.spawnplacement;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrozenSpawnPlacementTypes {
	public static final SpawnPlacementType ON_GROUND_OR_ON_LAVA_SURFACE = new SpawnPlacementType() {
		@Override
		public boolean isSpawnPositionOk(LevelReader levelReader, BlockPos blockPos, @Nullable EntityType<?> entityType) {
			if (entityType != null && levelReader.getWorldBorder().isWithinBounds(blockPos)) {
				BlockPos abovePos = blockPos.above();
				BlockPos belowPos = blockPos.below();
				BlockState belowState = levelReader.getBlockState(belowPos);
				if (!belowState.isValidSpawn(levelReader, belowPos, entityType) && !belowState.getFluidState().is(FluidTags.LAVA)) {
					return false;
				} else {
					return this.isValidEmptySpawnBlock(levelReader, blockPos, entityType) && this.isValidEmptySpawnBlock(levelReader, abovePos, entityType);
				}
			}
			return false;
		}

		private boolean isValidEmptySpawnBlock(@NotNull LevelReader levelReader, BlockPos blockPos, EntityType<?> entityType) {
			BlockState blockState = levelReader.getBlockState(blockPos);
			boolean isSafeBurning = blockState.is(BlockTags.FIRE) || blockState.is(Blocks.LAVA);
			return isSafeBurning || NaturalSpawner.isValidEmptySpawnBlock(levelReader, blockPos, blockState, blockState.getFluidState(), entityType);
		}

		@NotNull
		@Override
		public BlockPos adjustSpawnPosition(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos) {
			BlockPos belowPos = blockPos.below();
			return levelReader.getBlockState(belowPos).isPathfindable(PathComputationType.LAND) ? belowPos : blockPos;
		}
	};

}
