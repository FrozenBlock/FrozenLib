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
import net.frozenblock.lib.worldgen.feature.api.feature.config.CurvingSpikeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public class CurvingSpikeFeature extends Feature<CurvingSpikeConfig> {
	private static final int BELOW_HEIGHT = -4;

	public CurvingSpikeFeature(Codec<CurvingSpikeConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<CurvingSpikeConfig> context) {
		final WorldGenLevel level = context.level();
		final BlockPos pos = context.origin();
		final RandomSource random = context.random();
		final CurvingSpikeConfig config = context.config();

		final int height = config.height().sample(random);
		final double curveDistance = config.curveDistance().sample(random);
		final double curveFactorX = random.nextGaussian();
		final int xWidth = config.xWidth().sample(random);
		final double curveFactorZ = random.nextGaussian();
		final int zWidth = config.zWidth().sample(random);
		final BlockStateProvider stateProvider = config.stateProvider();
		final BlockPredicate replaceable = config.replaceable();

		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int i = 0; i < height; i++) {
			mutable.setWithOffset(pos, 0, i, 0);
			placeInSquare(
				level,
				mutable,
				stateProvider,
				replaceable,
				(double) i / height,
				curveDistance,
				curveFactorX,
				xWidth,
				curveFactorZ,
				zWidth,
				random
			);
		}

		for (int i = BELOW_HEIGHT; i < 0; i++) {
			mutable.setWithOffset(pos, 0, i, 0);
			placeInSquare(
				level,
				mutable,
				stateProvider,
				replaceable,
				(double) i / BELOW_HEIGHT,
				curveDistance,
				curveFactorX,
				xWidth,
				curveFactorZ,
				zWidth,
				random
			);
		}

		return true;
	}

	protected static void placeInSquare(
		WorldGenLevel level,
		BlockPos pos,
		BlockStateProvider stateProvider,
		BlockPredicate replaceable,
		double heightProgress,
		double curveDistance,
		double curveFactorX,
		int xWidth,
		double curveFactorZ,
		int zWidth,
		RandomSource random
	) {
		final double heightBasedCurveDistance = curveDistance * heightProgress;
		final double xCurve = curveFactorX * heightBasedCurveDistance;
		final double zCurve = curveFactorZ * heightBasedCurveDistance;
		final double inverseHeightProgress = 1D - heightProgress;
		final double xWidthDistance = xWidth * inverseHeightProgress;
		final double zWidthDistance = zWidth * inverseHeightProgress;

		final Vec3 centerPos = pos.getCenter().add(xCurve, 0D, zCurve);
		for (double xOffset = -(xWidth + 0.5D); xOffset <= xWidth + 0.5D; xOffset += 0.1D) {
			for (double zOffset = -(zWidth + 0.5D); zOffset <= zWidth + 0.5D; zOffset += 0.1D) {
				Vec3 offsetPos = new Vec3(
					pos.getX() + xOffset + xCurve,
					pos.getY() + 0.5D,
					pos.getZ() + zOffset + zCurve
				);

				if (!centerPos.closerThan(offsetPos, xWidthDistance)) continue;
				if (!centerPos.closerThan(offsetPos, zWidthDistance)) continue;

				final BlockPos placementPos = BlockPos.containing(offsetPos);
				if (!replaceable.test(level, placementPos)) continue;
				level.setBlock(placementPos, stateProvider.getState(random, placementPos), Block.UPDATE_CLIENTS);
			}
		}
	}
}
