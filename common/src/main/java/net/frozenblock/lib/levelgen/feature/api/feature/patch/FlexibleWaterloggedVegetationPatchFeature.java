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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class FlexibleWaterloggedVegetationPatchFeature extends FlexibleVegetationPatchFeature {
	public static final MapCodec<FlexibleWaterloggedVegetationPatchFeature> CODEC = makeCodec(FlexibleWaterloggedVegetationPatchFeature::new);

	public FlexibleWaterloggedVegetationPatchFeature(
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
		final Set<BlockPos> waterSurface = new HashSet<>();
		final BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();

		for (BlockPos surfacePos : surface) {
			if (!WaterloggedVegetationPatchFeature.isExposed(level, surface, surfacePos, testPos)) waterSurface.add(surfacePos);
		}

		for (BlockPos surfacePos : waterSurface) level.setBlock(surfacePos, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);

		return waterSurface;
	}

	@Override
	protected boolean placeVegetation(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos placementPos) {
		if (!super.placeVegetation(level, generator, random, placementPos.below())) return false;

		final BlockState placed = level.getBlockState(placementPos);
		if (placed.hasProperty(BlockStateProperties.WATERLOGGED) && !placed.getValue(BlockStateProperties.WATERLOGGED)) {
			level.setBlock(placementPos, placed.setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_CLIENTS);
		}
		return true;
	}
}
