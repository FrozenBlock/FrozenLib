/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathTagFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.NotNull;

public class NoisePathTagUnderWaterFeature extends Feature<PathTagFeatureConfig> {

    public NoisePathTagUnderWaterFeature(Codec<PathTagFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<PathTagFeatureConfig> context) {
        boolean generated = false;
		PathTagFeatureConfig config = context.config();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        ImprovedNoise sampler = config.noise() == 1 ? EasyNoiseSampler.perlinLocal : config.noise() == 2 ? EasyNoiseSampler.perlinChecked : config.noise() == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
        float chance = config.placementChance();
		BlockPos.MutableBlockPos mutable = blockPos.mutable();
        int bx = mutable.getX();
		int by = mutable.getY();
        int bz = mutable.getZ();
        int radiusSquared = config.radius() * config.radius();
        RandomSource random = level.getRandom();
		BlockPredicate predicate = config.onlyPlaceWhenExposed() ? BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE : BlockPredicate.alwaysTrue();

		for (int x = bx - config.radius(); x <= bx + config.radius(); x++) {
			for (int z = bz - config.radius(); z <= bz + config.radius(); z++) {
				if (!config.is3D()) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radiusSquared) {
						mutable.set(x, level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1, z);
						double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
						if (sample > config.minThreshold() && sample < config.maxThreshold() && level.getBlockState(mutable).is(config.replaceableBlocks()) && checkSurroundingBlocks(level, mutable, predicate) && isWaterNearby(level, mutable, 2) && random.nextFloat() <= chance) {
							generated = true;
							level.setBlock(mutable, config.state().getState(random, mutable), 3);
						}
					}
				} else {
					for (int y = by - config.radius(); y <= by + config.radius(); y++) {
						double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
						if (distance < radiusSquared) {
							mutable.set(x, y, z);
							double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
							if (sample > config.minThreshold() && sample < config.maxThreshold() && level.getBlockState(mutable).is(config.replaceableBlocks()) && checkSurroundingBlocks(level, mutable, predicate) && isWaterNearby(level, mutable, 2) && random.nextFloat() <= chance) {
								generated = true;
								level.setBlock(mutable, config.state().getState(random, mutable), 3);
							}
						}
					}
				}
			}
		}
        return generated;
    }

	private static boolean checkSurroundingBlocks(WorldGenLevel level, BlockPos pos, BlockPredicate predicate) {
		for (Direction direction : Direction.values()) {
			if (predicate.test(level, pos.relative(direction))) {
				return true;
			}
		}
		return false;
	}

    public static boolean isWaterNearby(WorldGenLevel level, @NotNull BlockPos blockPos, int x) {
        Iterator<BlockPos> var2 = BlockPos.betweenClosed(blockPos.offset(-x, -x, -x), blockPos.offset(x, x, x)).iterator();
        BlockPos blockPos2;
        do {
            if (!var2.hasNext()) {
                return false;
            }
            blockPos2 = var2.next();
        } while (!level.getBlockState(blockPos2).is(Blocks.WATER));
        return true;
    }

}
