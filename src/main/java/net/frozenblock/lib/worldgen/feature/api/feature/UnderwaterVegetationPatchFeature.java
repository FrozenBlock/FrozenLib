/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class UnderwaterVegetationPatchFeature extends VegetationPatchFeature {

	public UnderwaterVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	public Set<BlockPos> placeGroundPatch(
		WorldGenLevel level, VegetationPatchConfiguration config, RandomSource random, BlockPos blockPos, Predicate<BlockState> replaceable, int xRadius, int zRadius
	) {
		final BlockPos.MutableBlockPos airMutable = blockPos.mutable();
		final BlockPos.MutableBlockPos groundMutable = airMutable.mutable();
		final Direction surfaceDirection = config.surface.getDirection();
		final Direction oppositeSurfaceDirection = surfaceDirection.getOpposite();
		final Set<BlockPos> set = new HashSet<>();

		for (int x = -xRadius; x <= xRadius; x++) {
			boolean onEdgeX = x == -xRadius || x == xRadius;
			for (int z = -zRadius; z <= zRadius; z++) {
				boolean onEdgeZ = z == -zRadius || z == zRadius;
				boolean onAnyEdge = onEdgeX || onEdgeZ;
				boolean onBothEdges = onEdgeX && onEdgeZ;
				boolean onOneEdge = onAnyEdge && !onBothEdges;

				if (onBothEdges || !(!onOneEdge || config.extraEdgeColumnChance != 0F && !(random.nextFloat() > config.extraEdgeColumnChance))) continue;

				airMutable.setWithOffset(blockPos, x, 0, z);
				for (int verticalSteps = 0; level.isStateAtPosition(airMutable, this::isWaterAt) && verticalSteps < config.verticalRange; verticalSteps++) {
					airMutable.move(surfaceDirection);
				}

				for (int verticalSteps = 0; level.isStateAtPosition(airMutable, state -> !this.isWaterAt(state)) && verticalSteps < config.verticalRange; verticalSteps++) {
					airMutable.move(oppositeSurfaceDirection);
				}

				groundMutable.setWithOffset(airMutable, config.surface.getDirection());
				final BlockState state = level.getBlockState(groundMutable);
				if (!this.isWaterAt(level.getBlockState(airMutable)) || !state.isFaceSturdy(level, groundMutable, config.surface.getDirection().getOpposite())) continue;

				final int depth = config.depth.sample(random) + (config.extraBottomBlockChance > 0F && random.nextFloat() < config.extraBottomBlockChance ? 1 : 0);
				final BlockPos groundPos = groundMutable.immutable();
				final boolean placedGround = this.placeGround(level, config, replaceable, random, groundMutable, depth);
				if (placedGround) set.add(groundPos);
			}
		}

		return set;
	}

	public boolean isWaterAt(BlockState state) {
		return state.is(Blocks.WATER);
	}
}
