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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class VegetationPatchWithEdgeDecorationFeature extends VegetationPatchFeature {

	public VegetationPatchWithEdgeDecorationFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	protected void distributeVegetation(
		FeaturePlaceContext<VegetationPatchConfiguration> context,
		WorldGenLevel level,
		VegetationPatchConfiguration config,
		RandomSource random,
		Set<BlockPos> set,
		int xRadius,
		int zRadius
	) {
		final BlockPos.MutableBlockPos airMutable = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos groundMutable = new BlockPos.MutableBlockPos();
		final List<BlockPos> finalDecorationPoses = new ArrayList<>(set);
		final Direction surfaceDirection = config.surface.getDirection();
		final Direction oppositeDirection = surfaceDirection.getOpposite();

		for (BlockPos blockPos : set) {
			airMutable.setWithOffset(blockPos, oppositeDirection);
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				airMutable.move(direction);
				groundMutable.setWithOffset(airMutable, surfaceDirection);
				final BlockPos groundPos = groundMutable.immutable();

				if (!finalDecorationPoses.contains(groundPos)) {
					final BlockState groundState = level.getBlockState(groundPos);
					if (level.isEmptyBlock(airMutable) && groundState.isFaceSturdy(level, groundMutable, oppositeDirection)) finalDecorationPoses.add(groundPos);
				}

				airMutable.move(direction.getOpposite());
			}
		}

		set = new HashSet<>(finalDecorationPoses);
		super.distributeVegetation(context, level, config, random, set, xRadius, zRadius);
	}
}
