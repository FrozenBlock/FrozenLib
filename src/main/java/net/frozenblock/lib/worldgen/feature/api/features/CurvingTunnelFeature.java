/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.concurrent.atomic.AtomicBoolean;
import net.frozenblock.lib.worldgen.feature.api.features.config.CurvingTunnelFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class CurvingTunnelFeature extends Feature<CurvingTunnelFeatureConfig> {

	public CurvingTunnelFeature(Codec<CurvingTunnelFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<CurvingTunnelFeatureConfig> context) {
		AtomicBoolean generated = new AtomicBoolean(false);
		CurvingTunnelFeatureConfig config = context.config();
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		int radius = config.radius();
		int radiusSquared = radius * radius;
		RandomSource random = level.getRandom();
		double curvatureDifference = config.maxCurvature() - config.minCurvature();
		if (curvatureDifference < 0) {
			throw new UnsupportedOperationException("minCurvature can not be higher than maxCurvature!");
		}
		int bx = blockPos.getX();
		int by = blockPos.getY();
		int bz = blockPos.getZ();
		BlockPos.MutableBlockPos mutable = blockPos.mutable();
		BlockPos endPos = level.getHeightmapPos(Types.OCEAN_FLOOR_WG, blockPos);
		int endY = endPos.getY();
		int yDifference = endY - by;
		double xCurvature = ((random.nextDouble() * curvatureDifference) + config.minCurvature()) * (random.nextBoolean() ? 1 : -1);
		double zCurvature = ((random.nextDouble() * curvatureDifference) + config.minCurvature()) * (random.nextBoolean() ? 1 : -1);

		for (int yOffset = 0; yOffset < yDifference; yOffset++) {
			int y = by + yOffset;
			double curvatureProgress = Math.sin(((double) yOffset / yDifference) * Math.PI);
			int xOffset = (int) (curvatureProgress * xCurvature);
			int zOffset = (int) (curvatureProgress * zCurvature);
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					double distance = ((-x) * (-x) + (-z) * (-z));
					if (distance <= radiusSquared) {
						mutable.set(bx + x, y, bz + z);
						mutable.move(xOffset, 0, zOffset);
						if (level.getBlockState(mutable).is(config.replaceableBlocks())) {
							level.setBlock(mutable, config.state().getState(random, mutable), Block.UPDATE_ALL);
							generated.set(true);
						}
					}
				}
			}
		}
		return generated.get();
	}

}
