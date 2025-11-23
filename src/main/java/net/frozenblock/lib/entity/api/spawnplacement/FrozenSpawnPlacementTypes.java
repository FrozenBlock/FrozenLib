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
import org.jetbrains.annotations.Nullable;

public class FrozenSpawnPlacementTypes {
	/**
	 * A {@link SpawnPlacementType} that spawns entities either on the ground or on the surface of Lava.
	 */
	public static final SpawnPlacementType ON_GROUND_OR_ON_LAVA_SURFACE = new SpawnPlacementType() {
		@Override
		public boolean isSpawnPositionOk(LevelReader level, BlockPos pos, @Nullable EntityType<?> entityType) {
			if (entityType == null || !level.getWorldBorder().isWithinBounds(pos)) return false;

			final BlockPos belowPos = pos.below();
			final BlockState belowState = level.getBlockState(belowPos);
			if (!belowState.isValidSpawn(level, belowPos, entityType) && !this.isSurfaceLavaOrMagma(level, pos, belowState)) return false;
			return this.isValidEmptySpawnBlock(level, pos, entityType);
		}

		private boolean isSurfaceLavaOrMagma(LevelReader level, BlockPos pos, BlockState belowState) {
			final BlockState state = level.getBlockState(pos);
			return (belowState.getFluidState().is(FluidTags.LAVA) || belowState.is(Blocks.MAGMA_BLOCK))
				&& !(state.getFluidState().is(FluidTags.LAVA) || state.is(Blocks.MAGMA_BLOCK));
		}

		private boolean isValidEmptySpawnBlock(LevelReader level, BlockPos pos, EntityType<?> entityType) {
			final BlockState state = level.getBlockState(pos);
			final boolean isSafeBurning = state.is(BlockTags.FIRE);
			return isSafeBurning || NaturalSpawner.isValidEmptySpawnBlock(level, pos, state, state.getFluidState(), entityType);
		}

		@Override
		public BlockPos adjustSpawnPosition(LevelReader level, BlockPos pos) {
			final BlockPos belowPos = pos.below();
			return level.getBlockState(belowPos).isPathfindable(PathComputationType.LAND) ? belowPos : pos;
		}
	};

}
