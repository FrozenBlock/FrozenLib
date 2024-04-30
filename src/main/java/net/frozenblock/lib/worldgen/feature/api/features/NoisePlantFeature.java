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
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.NotNull;

public class NoisePlantFeature extends Feature<PathFeatureConfig> {

    public NoisePlantFeature(Codec<PathFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<PathFeatureConfig> context) {
        boolean generated = false;
        PathFeatureConfig config = context.config();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        int radiusSquared = config.radius() * config.radius();
        RandomSource random = level.getRandom();
        ImprovedNoise sampler = config.noise() == 1 ? EasyNoiseSampler.perlinLocal : config.noise() == 2 ? EasyNoiseSampler.perlinChecked : config.noise() == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
        float chance = config.placementChance();
		int bx = blockPos.getX();
        int bz = blockPos.getZ();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();

        for (int x = bx - config.radius(); x <= bx + config.radius(); x++) {
            for (int z = bz - config.radius(); z <= bz + config.radius(); z++) {
                double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
                if (distance < radiusSquared) {
                    mutable.set(x, level.getHeight(Types.OCEAN_FLOOR, x, z), z);
                    double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
                    if (sample > config.minThreshold() && sample < config.maxThreshold() && level.getBlockState(mutable).is(config.replaceableBlocks()) && level.getBlockState(mutable.below()).is(BlockTags.DIRT) && random.nextFloat() <= chance) {
                        generated = true;
                        level.setBlock(mutable, config.state().getState(random, mutable), 3);
                    }
                }
            }
        }
        return generated;
    }

}
