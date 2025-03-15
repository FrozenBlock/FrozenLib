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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class UnderwaterVegetationPatchFeature extends VegetationPatchFeature {

	public UnderwaterVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	public @NotNull Set<BlockPos> placeGroundPatch(
		@NotNull WorldGenLevel worldGenLevel,
		@NotNull VegetationPatchConfiguration vegetationPatchConfiguration,
		@NotNull RandomSource randomSource,
		@NotNull BlockPos blockPos,
		@NotNull Predicate<BlockState> canReplace,
		int i,
		int j
	) {
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		BlockPos.MutableBlockPos mutableBlockPos2 = mutableBlockPos.mutable();
		Direction surfaceDirection = vegetationPatchConfiguration.surface.getDirection();
		Direction oppositeDirection = surfaceDirection.getOpposite();
		Set<BlockPos> set = new HashSet<>();

		for (int k = -i; k <= i; k++) {
			boolean bl = k == -i || k == i;

			for (int l = -j; l <= j; l++) {
				boolean bl2 = l == -j || l == j;
				boolean bl3 = bl || bl2;
				boolean bl4 = bl && bl2;
				boolean bl5 = bl3 && !bl4;
				if (!bl4 && (!bl5 || vegetationPatchConfiguration.extraEdgeColumnChance != 0F && !(randomSource.nextFloat() > vegetationPatchConfiguration.extraEdgeColumnChance))) {
					mutableBlockPos.setWithOffset(blockPos, k, 0, l);

					for (int verticalSteps = 0;
						 worldGenLevel.isStateAtPosition(mutableBlockPos, this::isWaterAt)
							 && verticalSteps < vegetationPatchConfiguration.verticalRange;
						 verticalSteps++
					) {
						mutableBlockPos.move(surfaceDirection);
					}

					for (int verticalSteps = 0;
						 worldGenLevel.isStateAtPosition(mutableBlockPos, blockStatex -> !this.isWaterAt(blockStatex))
							 && verticalSteps < vegetationPatchConfiguration.verticalRange;
						 verticalSteps++
					) {
						mutableBlockPos.move(oppositeDirection);
					}

					mutableBlockPos2.setWithOffset(mutableBlockPos, vegetationPatchConfiguration.surface.getDirection());
					BlockState blockState = worldGenLevel.getBlockState(mutableBlockPos2);
					if (this.isWaterAt(worldGenLevel.getBlockState(mutableBlockPos))
						&& blockState.isFaceSturdy(worldGenLevel, mutableBlockPos2, vegetationPatchConfiguration.surface.getDirection().getOpposite())
					) {
						int depth = vegetationPatchConfiguration.depth.sample(randomSource)
							+ (vegetationPatchConfiguration.extraBottomBlockChance > 0F && randomSource.nextFloat() < vegetationPatchConfiguration.extraBottomBlockChance ? 1 : 0);
						BlockPos blockPos2 = mutableBlockPos2.immutable();
						boolean placedGround = this.placeGround(worldGenLevel, vegetationPatchConfiguration, canReplace, randomSource, mutableBlockPos2, depth);
						if (placedGround) set.add(blockPos2);
					}
				}
			}
		}

		return set;
	}

	public boolean isWaterAt(@NotNull BlockState blockState) {
		return blockState.is(Blocks.WATER);
	}
}
