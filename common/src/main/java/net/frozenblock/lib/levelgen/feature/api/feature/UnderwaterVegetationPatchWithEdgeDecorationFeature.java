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

package net.frozenblock.lib.levelgen.feature.api.feature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class UnderwaterVegetationPatchWithEdgeDecorationFeature extends UnderwaterVegetationPatchFeature {
	public static final MapCodec<UnderwaterVegetationPatchWithEdgeDecorationFeature> CODEC = makeCodec(UnderwaterVegetationPatchWithEdgeDecorationFeature::new);

	public UnderwaterVegetationPatchWithEdgeDecorationFeature(
		HolderSet<Block> replaceable,
		Holder<BlockStateProvider> groundState,
		Holder<PlacedFeature> vegetationFeature,
		CaveSurface surface,
		IntProvider depth,
		float extraBottomBlockChance,
		int verticalRange,
		float vegetationChance,
		IntProvider xzRadius,
		float extraEdgeColumnChance
	) {
		super(replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance);
	}

	@Override
	public MapCodec<? extends VegetationPatchFeature> codec() {
		return CODEC;
	}

	@Override
	public void distributeVegetation(WorldGenLevel level, ChunkGenerator generator, RandomSource random, Set<BlockPos> surface) {
		final BlockPos.MutableBlockPos airMutable = new BlockPos.MutableBlockPos();
		final BlockPos.MutableBlockPos groundMutable = new BlockPos.MutableBlockPos();
		final List<BlockPos> finalDecorationPoses = new ArrayList<>(surface);
		final Direction inwards = this.surface.getDirection();
		final Direction outwards = inwards.getOpposite();

		for (BlockPos pos : surface) {
			airMutable.setWithOffset(pos, outwards);
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				airMutable.move(direction);
				groundMutable.setWithOffset(airMutable, inwards);
				final BlockPos groundPos = groundMutable.immutable();

				if (!finalDecorationPoses.contains(groundPos)) {
					final BlockState groundState = level.getBlockState(groundPos);
					if (this.isWaterAt(level.getBlockState(airMutable)) && groundState.isFaceSturdy(level, groundMutable, outwards)) {
						finalDecorationPoses.add(groundPos);
					}
				}

				airMutable.move(direction.getOpposite());
			}
		}

		surface = new HashSet<>(finalDecorationPoses);
		super.distributeVegetation(level, generator, random, surface);
	}
}
