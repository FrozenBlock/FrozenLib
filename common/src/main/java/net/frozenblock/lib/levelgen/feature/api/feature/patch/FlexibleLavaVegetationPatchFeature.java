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

package net.frozenblock.lib.levelgen.feature.api.feature.patch;

import com.mojang.serialization.MapCodec;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.WaterloggedVegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FlexibleLavaVegetationPatchFeature extends FlexibleVegetationPatchFeature {
	public static final MapCodec<FlexibleLavaVegetationPatchFeature> CODEC = makeCodec(FlexibleLavaVegetationPatchFeature::new);

	public FlexibleLavaVegetationPatchFeature(
		HolderSet<Block> replaceable,
		Holder<BlockStateProvider> groundState,
		Holder<PlacedFeature> vegetationFeature,
		CaveSurface surface,
		IntProvider depth,
		float extraBottomBlockChance,
		int verticalRange,
		float vegetationChance,
		IntProvider xzRadius,
		float extraEdgeColumnChance,
		FlexibleComponents flexibleComponents
	) {
		super(
			replaceable,
			groundState,
			vegetationFeature,
			surface,
			depth,
			extraBottomBlockChance,
			verticalRange,
			vegetationChance,
			xzRadius,
			extraEdgeColumnChance,
			flexibleComponents
		);
	}

	@Override
	public MapCodec<? extends FlexibleVegetationPatchFeature> codec() {
		return CODEC;
	}

	@Override
	public Set<BlockPos> placeGroundPatch(WorldGenLevel level, RandomSource random, BlockPos origin, Predicate<BlockState> replaceable, int xRadius, int zRadius) {
		final Set<BlockPos> surface = super.placeGroundPatch(level, random, origin, replaceable, xRadius, zRadius);
		final Set<BlockPos> lavaSurface = new HashSet<>();
		final BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();

		for (BlockPos surfacePos : surface) {
			if (!WaterloggedVegetationPatchFeature.isExposed(level, surface, surfacePos, testPos)) lavaSurface.add(surfacePos);
		}

		for (BlockPos surfacePos : lavaSurface) level.setBlock(surfacePos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);

		return lavaSurface;
	}

	@Override
	protected boolean placeVegetation(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos placementPos) {
		return super.placeVegetation(level, generator, random, placementPos.below());
	}
}
