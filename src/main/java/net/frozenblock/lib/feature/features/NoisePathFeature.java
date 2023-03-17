/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.feature.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.feature.features.config.PathFeatureConfig;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public class NoisePathFeature extends Feature<PathFeatureConfig> {
    public NoisePathFeature(Codec<PathFeatureConfig> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<PathFeatureConfig> context) {
        boolean generated = false;
        PathFeatureConfig config = context.config();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        int radiusSquared = config.radius * config.radius;
        RandomSource random = level.getRandom();
        ImprovedNoise sampler = config.noise == 1 ? EasyNoiseSampler.perlinLocal : config.noise == 2 ? EasyNoiseSampler.perlinChecked : config.noise == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
        int bx = blockPos.getX();
		int by = blockPos.getY();
        int bz = blockPos.getZ();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
		BlockPredicate predicate = config.onlyExposed ? BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE : BlockPredicate.alwaysTrue();

        for (int x = bx - config.radius; x <= bx + config.radius; x++) {
            for (int z = bz - config.radius; z <= bz + config.radius; z++) {
				if (!config.is3D) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radiusSquared) {
						mutable.set(x, level.getHeight(Types.OCEAN_FLOOR, x, z) - 1, z);
						double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.multiplier, config.multiplyY, config.useY);
						if (sample > config.minThresh && sample < config.maxThresh && level.getBlockState(mutable).is(config.replaceable) && checkSurroundingBlocks(level, mutable, predicate)) {
							generated = true;
							level.setBlock(mutable, config.pathBlock.getState(random, mutable), 3);
						}
					}
				} else {
					for (int y = by - config.radius; y <= by + config.radius; y++) {
						double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
						if (distance < radiusSquared) {
							mutable.set(x, y, z);
							double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.multiplier, config.multiplyY, config.useY);
							if (sample > config.minThresh && sample < config.maxThresh && level.getBlockState(mutable).is(config.replaceable) && checkSurroundingBlocks(level, mutable, predicate)) {
								generated = true;
								level.setBlock(mutable, config.pathBlock.getState(random, mutable), 3);
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

}
