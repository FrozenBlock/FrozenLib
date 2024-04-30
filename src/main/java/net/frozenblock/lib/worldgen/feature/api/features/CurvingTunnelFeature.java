/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.frozenblock.lib.worldgen.feature.api.features.config.CurvingTunnelFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
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

		Consumer<LevelAccessor> consumer = (levelAccessor) -> {
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
		};
		if (yDifference > 0) {
			level.getServer().executeBlocking(() -> consumer.accept(level.getLevel()));
		}
        return generated.get();
    }

}
