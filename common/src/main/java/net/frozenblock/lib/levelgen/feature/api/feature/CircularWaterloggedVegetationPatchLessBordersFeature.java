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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class CircularWaterloggedVegetationPatchLessBordersFeature extends VegetationPatchFeature {
	public static final MapCodec<CircularWaterloggedVegetationPatchLessBordersFeature> CODEC = makeCodec(CircularWaterloggedVegetationPatchLessBordersFeature::new);

	public CircularWaterloggedVegetationPatchLessBordersFeature(
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
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final int radius = this.xzRadius.sample(random) + 1;
		final Set<BlockPos> surface = this.placeGroundPatch(level, random, origin, state -> state.is(this.replaceable), radius, radius);
		this.distributeVegetation(level, chunkGenerator, random, surface);
		return !surface.isEmpty();
	}

	public Set<BlockPos> placeCircularGroundPatch(WorldGenLevel level, RandomSource random, BlockPos origin, Predicate<BlockState> replaceable, int xRadius, int zRadius) {
		final MutableBlockPos airMutable = origin.mutable();
		final MutableBlockPos groundMutable = airMutable.mutable();
		final Direction surfaceDirection = this.surface.getDirection();
		final Direction oppositeSurfaceDirection = surfaceDirection.getOpposite();
		final Set<BlockPos> set = new HashSet<>();

		for (int x = -xRadius; x <= xRadius; ++x) {
			for (int z = -zRadius; z <= zRadius; ++z) {
				airMutable.setWithOffset(origin, x, 0, z);
				if (Math.sqrt(airMutable.distSqr(origin)) > xRadius) continue;

				for (int i = 0; level.isStateAtPosition(airMutable, BlockBehaviour.BlockStateBase::isAir) && i < this.verticalRange; ++i) {
					airMutable.move(surfaceDirection);
				}

				for (int i = 0; level.isStateAtPosition(airMutable, (statex) -> !statex.isAir()) && i < this.verticalRange; ++i) {
					airMutable.move(oppositeSurfaceDirection);
				}

				groundMutable.setWithOffset(airMutable, this.surface.getDirection());
				final BlockState state = level.getBlockState(groundMutable);
				if (!level.isEmptyBlock(airMutable) || !state.isFaceSturdy(level, groundMutable, this.surface.getDirection().getOpposite())) continue;

				final int depth = this.depth.sample(random) + (this.extraBottomBlockChance > 0F && random.nextFloat() < this.extraBottomBlockChance ? 1 : 0);
				final BlockPos groundPos = groundMutable.immutable();
				final boolean placedGround = this.placeGround(level, replaceable, random, groundMutable, depth);
				if (placedGround) set.add(groundPos);
			}
		}

		return set;
	}

	@Override
	protected Set<BlockPos> placeGroundPatch(WorldGenLevel level, RandomSource random, BlockPos origin, Predicate<BlockState> replaceable, int xRadius, int zRadius) {
		final Set<BlockPos> surface = this.placeCircularGroundPatch(level, random, origin, replaceable, xRadius, zRadius);
		final Set<BlockPos> surroundedSurface = new HashSet<>();
		final MutableBlockPos mutable = new MutableBlockPos();

		for (BlockPos nextPos : surface) {
			if (!isExposed(level, nextPos, mutable)) surroundedSurface.add(nextPos);
		}

		for (BlockPos nextPos : surroundedSurface) level.setBlock(nextPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);

		return surroundedSurface;
	}

	private static boolean isExposed(WorldGenLevel level, BlockPos pos, MutableBlockPos mutable) {
		return isExposedDirection(level, pos, mutable, Direction.NORTH)
			|| isExposedDirection(level, pos, mutable, Direction.EAST)
			|| isExposedDirection(level, pos, mutable, Direction.SOUTH)
			|| isExposedDirection(level, pos, mutable, Direction.WEST)
			|| isExposedDirection(level, pos, mutable, Direction.DOWN);
	}

	private static boolean isExposedDirection(WorldGenLevel level, BlockPos pos, MutableBlockPos mutable, Direction direction) {
		mutable.setWithOffset(pos, direction);
		final BlockState state = level.getBlockState(mutable);
		return !state.isFaceSturdy(level, mutable, direction.getOpposite()) && !state.is(Blocks.WATER);
	}

	@Override
	protected boolean placeVegetation(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos vegetationPos) {
		if (!super.placeVegetation(level, chunkGenerator, random, vegetationPos.below())) return false;

		final BlockState state = level.getBlockState(vegetationPos);
		if (!state.hasProperty(BlockStateProperties.WATERLOGGED) || state.getValue(BlockStateProperties.WATERLOGGED)) return true;

		level.setBlock(vegetationPos, state.setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_CLIENTS);
		return true;
	}

	@Override
	public boolean placeGround(WorldGenLevel level, Predicate<BlockState> replaceable, RandomSource random, BlockPos.MutableBlockPos belowPos, int depth) {
		for (int i = 0; i < depth; ++i) {
			final BlockState state = level.getBlockState(belowPos);
			if (!replaceable.test(state)) return i != 0;
			belowPos.move(this.surface.getDirection());
		}
		return true;
	}
}
