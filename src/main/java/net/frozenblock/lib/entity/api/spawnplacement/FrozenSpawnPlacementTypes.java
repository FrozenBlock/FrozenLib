/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
				BlockPos belowPos = blockPos.below();
				BlockState belowState = levelReader.getBlockState(belowPos);
				if (!belowState.isValidSpawn(levelReader, belowPos, entityType) && !belowState.getFluidState().is(FluidTags.LAVA) && !belowState.is(Blocks.MAGMA_BLOCK)) {
					return false;
				} else {
					return this.isValidEmptySpawnBlock(levelReader, blockPos, entityType);
				}
			}
			return false;
		}

		private boolean isValidEmptySpawnBlock(@NotNull LevelReader levelReader, BlockPos blockPos, EntityType<?> entityType) {
			BlockState blockState = levelReader.getBlockState(blockPos);
			boolean isSafeBurning = blockState.is(BlockTags.FIRE);
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
