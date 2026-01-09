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
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class CircularWaterloggedVegetationPatchLessBordersFeature extends VegetationPatchFeature {

	public CircularWaterloggedVegetationPatchLessBordersFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> context) {
		final WorldGenLevel level = context.level();
		final VegetationPatchConfiguration config = context.config();
		final RandomSource random = context.random();
		final BlockPos pos = context.origin();
		final Predicate<BlockState> predicate = state -> state.is(config.replaceable);
		final int radius = config.xzRadius.sample(random) + 1;
		final Set<BlockPos> set = this.placeGroundPatch(level, config, random, pos, predicate, radius, radius);

		this.distributeVegetation(context, level, config, random, set, radius, radius);
		return !set.isEmpty();
	}

	public Set<BlockPos> placeCircularGroundPatch(
		WorldGenLevel level, VegetationPatchConfiguration config, RandomSource random, BlockPos pos, Predicate<BlockState> predicate, int xRadius, int zRadius
	) {
		final MutableBlockPos airMutable = pos.mutable();
		final MutableBlockPos groundMutable = airMutable.mutable();
		final Direction surfaceDirection = config.surface.getDirection();
		final Direction oppositeSurfaceDirection = surfaceDirection.getOpposite();
		final Set<BlockPos> set = new HashSet<>();

		for (int x = -xRadius; x <= xRadius; ++x) {
			for (int z = -zRadius; z <= zRadius; ++z) {
				airMutable.setWithOffset(pos, x, 0, z);
				if (Math.sqrt(airMutable.distSqr(pos)) > xRadius) continue;

				for (int i = 0; level.isStateAtPosition(airMutable, BlockBehaviour.BlockStateBase::isAir) && i < config.verticalRange; ++i) {
					airMutable.move(surfaceDirection);
				}

				for (int i = 0; level.isStateAtPosition(airMutable, (statex) -> !statex.isAir()) && i < config.verticalRange; ++i) {
					airMutable.move(oppositeSurfaceDirection);
				}

				groundMutable.setWithOffset(airMutable, config.surface.getDirection());
				final BlockState state = level.getBlockState(groundMutable);
				if (!level.isEmptyBlock(airMutable) || !state.isFaceSturdy(level, groundMutable, config.surface.getDirection().getOpposite())) continue;

				final int depth = config.depth.sample(random) + (config.extraBottomBlockChance > 0F && random.nextFloat() < config.extraBottomBlockChance ? 1 : 0);
				final BlockPos groundPos = groundMutable.immutable();
				final boolean placedGround = this.placeGround(level, config, predicate, random, groundMutable, depth);
				if (placedGround) set.add(groundPos);
			}
		}

		return set;
	}

	@Override
	protected Set<BlockPos> placeGroundPatch(
		WorldGenLevel level, VegetationPatchConfiguration config, RandomSource random, BlockPos pos, Predicate<BlockState> predicate, int xRadius, int zRadius
	) {
		final Set<BlockPos> set = this.placeCircularGroundPatch(level, config, random, pos, predicate, xRadius, zRadius);
		final Set<BlockPos> set2 = new HashSet<>();
		final MutableBlockPos mutable = new MutableBlockPos();

		Iterator<BlockPos> poses = set.iterator();
		while (poses.hasNext()) {
			BlockPos nextPos = poses.next();
			if (!isExposed(level, nextPos, mutable)) set2.add(nextPos);
		}

		poses = set2.iterator();
		while (poses.hasNext()) {
			BlockPos nextPos = poses.next();
			level.setBlock(nextPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);
		}

		return set2;
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
	protected boolean placeVegetation(WorldGenLevel level, VegetationPatchConfiguration config, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
		if (!super.placeVegetation(level, config, chunkGenerator, random, pos.below())) return false;

		final BlockState state = level.getBlockState(pos);
		if (!state.hasProperty(BlockStateProperties.WATERLOGGED) || state.getValue(BlockStateProperties.WATERLOGGED)) return true;

		level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), Block.UPDATE_CLIENTS);
		return true;
	}

	@Override
	protected boolean placeGround(
		WorldGenLevel level, VegetationPatchConfiguration config, Predicate<BlockState> replaceable, RandomSource random, BlockPos.MutableBlockPos mutable, int maxDistance
	) {
		for (int i = 0; i < maxDistance; ++i) {
			final BlockState state = level.getBlockState(mutable);
			if (!replaceable.test(state)) return i != 0;
			mutable.move(config.surface.getDirection());
		}
		return true;
	}
}
