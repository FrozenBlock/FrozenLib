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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import org.jetbrains.annotations.NotNull;

public class CircularLavaVegetationPatchFeature extends VegetationPatchFeature {

	public CircularLavaVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<VegetationPatchConfiguration> context) {
		WorldGenLevel worldGenLevel = context.level();
		VegetationPatchConfiguration vegetationPatchConfiguration = context.config();
		RandomSource randomSource = context.random();
		BlockPos blockPos = context.origin();
		Predicate<BlockState> predicate = (state) -> state.is(vegetationPatchConfiguration.replaceable);
		int radius = vegetationPatchConfiguration.xzRadius.sample(randomSource) + 1;
		Set<BlockPos> set = this.placeGroundPatch(worldGenLevel, vegetationPatchConfiguration, randomSource, blockPos, predicate, radius, radius);
		this.distributeVegetation(context, worldGenLevel, vegetationPatchConfiguration, randomSource, set, radius, radius);
		return !set.isEmpty();
	}

	public Set<BlockPos> placeCircularGroundPatch(WorldGenLevel level, @NotNull VegetationPatchConfiguration config, RandomSource random, @NotNull BlockPos pos, Predicate<BlockState> state, int xRadius, int zRadius) {
		MutableBlockPos mutableBlockPos = pos.mutable();
		MutableBlockPos mutableBlockPos2 = mutableBlockPos.mutable();
		Direction direction = config.surface.getDirection();
		Direction direction2 = direction.getOpposite();
		Set<BlockPos> set = new HashSet<>();

		for (int i = -xRadius; i <= xRadius; ++i) {
			boolean bl = i == -xRadius || i == xRadius;

			for (int j = -zRadius; j <= zRadius; ++j) {
				boolean bl2 = j == -zRadius || j == zRadius;
				boolean bl3 = bl || bl2;
				boolean bl4 = bl && bl2;
				boolean bl5 = bl3 && !bl4;
				if (!bl4 && (!bl5 || config.extraEdgeColumnChance != 0.0F && !(random.nextFloat() > config.extraEdgeColumnChance))) {
					mutableBlockPos.setWithOffset(pos, i, 0, j);

					if (Math.sqrt(mutableBlockPos.distSqr(pos)) <= xRadius) {
						int k;
						for (k = 0; level.isStateAtPosition(mutableBlockPos, BlockBehaviour.BlockStateBase::isAir) && k < config.verticalRange; ++k) {
							mutableBlockPos.move(direction);
						}

						for (k = 0; level.isStateAtPosition(mutableBlockPos, (statex) -> !statex.isAir()) && k < config.verticalRange; ++k) {
							mutableBlockPos.move(direction2);
						}

						mutableBlockPos2.setWithOffset(mutableBlockPos, config.surface.getDirection());
						BlockState blockState = level.getBlockState(mutableBlockPos2);
						if (level.isEmptyBlock(mutableBlockPos) && blockState.isFaceSturdy(level, mutableBlockPos2, config.surface.getDirection().getOpposite())) {
							int depth = config.depth.sample(random) + (config.extraBottomBlockChance > 0F && random.nextFloat() < config.extraBottomBlockChance ? 1 : 0);
							BlockPos blockPos = mutableBlockPos2.immutable();
							boolean placedGround = this.placeGround(level, config, state, random, mutableBlockPos2, depth);
							if (placedGround) set.add(blockPos);
						}
					}
				}
			}
		}

		return set;
	}

	@Override
	protected Set<BlockPos> placeGroundPatch(WorldGenLevel level, VegetationPatchConfiguration config, RandomSource random, BlockPos pos, Predicate<BlockState> state, int xRadius, int zRadius) {
		Set<BlockPos> set = this.placeCircularGroundPatch(level, config, random, pos, state, xRadius, zRadius);
		Set<BlockPos> set2 = new HashSet<>();
		MutableBlockPos mutableBlockPos = new MutableBlockPos();
		Iterator<BlockPos> var11 = set.iterator();

		BlockPos blockPos;
		while (var11.hasNext()) {
			blockPos = var11.next();
			if (!isExposed(level, blockPos, mutableBlockPos)) set2.add(blockPos);
		}

		var11 = set2.iterator();

		while (var11.hasNext()) {
			blockPos = var11.next();
			level.setBlock(blockPos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);
		}

		return set2;
	}

	private static boolean isExposed(WorldGenLevel level, BlockPos pos, MutableBlockPos mutablePos) {
		return isExposedDirection(level, pos, mutablePos, Direction.NORTH) || isExposedDirection(level, pos, mutablePos, Direction.EAST) || isExposedDirection(level, pos, mutablePos, Direction.SOUTH) || isExposedDirection(level, pos, mutablePos, Direction.WEST) || isExposedDirection(level, pos, mutablePos, Direction.DOWN);
	}

	private static boolean isExposedDirection(@NotNull WorldGenLevel level, BlockPos pos, @NotNull MutableBlockPos mutablePos, Direction direction) {
		mutablePos.setWithOffset(pos, direction);
		return !level.getBlockState(mutablePos).isFaceSturdy(level, mutablePos, direction.getOpposite());
	}

	@Override
	protected boolean placeVegetation(WorldGenLevel level, VegetationPatchConfiguration config, ChunkGenerator chunkGenerator, RandomSource random, @NotNull BlockPos pos) {
		return super.placeVegetation(level, config, chunkGenerator, random, pos.below());
	}
}
