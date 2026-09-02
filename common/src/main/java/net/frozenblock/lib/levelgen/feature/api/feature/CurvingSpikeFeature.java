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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public record CurvingSpikeFeature(
	Holder<BlockStateProvider> stateProvider,
	IntProvider xWidth,
	IntProvider zWidth,
	IntProvider height,
	FloatProvider curveDistance,
	BlockPredicate replaceable
) implements Feature {
	private static final int BELOW_HEIGHT = -4;
	public static final MapCodec<CurvingSpikeFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockStateProvider.CODEC.fieldOf("state").forGetter(CurvingSpikeFeature::stateProvider),
		IntProviders.codec(1, 12).fieldOf("x_width").forGetter(CurvingSpikeFeature::xWidth),
		IntProviders.codec(1, 12).fieldOf("z_width").forGetter(CurvingSpikeFeature::zWidth),
		IntProviders.codec(1, 32).fieldOf("height").forGetter(CurvingSpikeFeature::height),
		FloatProviders.codec(-4F, 4F).fieldOf("curve_distance").forGetter(CurvingSpikeFeature::curveDistance),
		BlockPredicate.CODEC.fieldOf("replaceable").forGetter(CurvingSpikeFeature::replaceable)
	).apply(instance, CurvingSpikeFeature::new));

	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final int height = this.height.sample(random);
		final double curveDistance = this.curveDistance.sample(random);
		final double curveFactorX = random.nextGaussian();
		final int xWidth = this.xWidth.sample(random);
		final double curveFactorZ = random.nextGaussian();
		final int zWidth = this.zWidth.sample(random);

		final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int i = 0; i < height; i++) {
			mutable.setWithOffset(origin, 0, i, 0);
			placeInSquare(
				level,
				mutable,
				this.stateProvider.value(),
				this.replaceable,
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
			mutable.setWithOffset(origin, 0, i, 0);
			placeInSquare(
				level,
				mutable,
				this.stateProvider.value(),
				this.replaceable,
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

		final Vec3 centerPos = Vec3.atCenterOf(pos).add(xCurve, 0D, zCurve);
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
				level.setBlock(placementPos, stateProvider.getState(level, random, placementPos), Block.UPDATE_CLIENTS);
			}
		}
	}
}
