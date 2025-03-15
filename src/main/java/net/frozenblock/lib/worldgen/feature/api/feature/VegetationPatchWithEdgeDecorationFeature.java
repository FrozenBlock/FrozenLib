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

package net.frozenblock.lib.worldgen.feature.api.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VegetationPatchWithEdgeDecorationFeature extends VegetationPatchFeature {

	public VegetationPatchWithEdgeDecorationFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	protected void distributeVegetation(
		@NotNull FeaturePlaceContext<VegetationPatchConfiguration> featurePlaceContext,
		@NotNull WorldGenLevel worldGenLevel,
		@NotNull VegetationPatchConfiguration vegetationPatchConfiguration,
		@NotNull RandomSource randomSource,
		@NotNull Set<BlockPos> set,
		int i,
		int j
	) {
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();
		List<BlockPos> finalDecorationPoses = new ArrayList<>(set);
		Direction surfaceDirection = vegetationPatchConfiguration.surface.getDirection();
		Direction oppositeDirection = surfaceDirection.getOpposite();

		for (BlockPos blockPos : set) {
			mutableBlockPos.setWithOffset(blockPos, oppositeDirection);
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				mutableBlockPos.move(direction);
				mutableBlockPos2.setWithOffset(mutableBlockPos, surfaceDirection);
				BlockPos belowPos = mutableBlockPos2.immutable();

				if (!finalDecorationPoses.contains(belowPos)) {
					BlockState blockState = worldGenLevel.getBlockState(belowPos);
					if (worldGenLevel.isEmptyBlock(mutableBlockPos)
						&& blockState.isFaceSturdy(worldGenLevel, mutableBlockPos2, oppositeDirection)
					) {
						finalDecorationPoses.add(belowPos);
					}
				}
				mutableBlockPos.move(direction.getOpposite());
			}
		}

		set = new HashSet<>(finalDecorationPoses);

		super.distributeVegetation(featurePlaceContext, worldGenLevel, vegetationPatchConfiguration, randomSource, set, i, j);
	}
}
