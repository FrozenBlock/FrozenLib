/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathSwapUnderWaterFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.NotNull;

public class NoisePathSwapUnderWaterFeature extends Feature<PathSwapUnderWaterFeatureConfig> {

    public NoisePathSwapUnderWaterFeature(Codec<PathSwapUnderWaterFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<PathSwapUnderWaterFeatureConfig> context) {
        boolean generated = false;
		PathSwapUnderWaterFeatureConfig config = context.config();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        int radiusSquared = config.radius() * config.radius();
        RandomSource random = level.getRandom();
        ImprovedNoise sampler = config.noise() == 1 ? EasyNoiseSampler.perlinLocal : config.noise() == 2 ? EasyNoiseSampler.perlinChecked : config.noise() == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
        float chance = config.placement_chance();
		int bx = blockPos.getX();
		int by = blockPos.getY();
        int bz = blockPos.getZ();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
		BlockPredicate predicate = config.onlyPlaceWhenExposed() ? BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE : BlockPredicate.alwaysTrue();

		for (int x = bx - config.radius(); x <= bx + config.radius(); x++) {
			for (int z = bz - config.radius(); z <= bz + config.radius(); z++) {
				if (!config.is3D()) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radiusSquared) {
						mutable.set(x, level.getHeight(Types.OCEAN_FLOOR, x, z) - 1, z);
						double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
						if (sample > config.minThreshold() && sample < config.maxThreshold() && level.getBlockState(mutable).is(config.replaceableBlocks()) && checkSurroundingBlocks(level, mutable, predicate) && random.nextFloat() <= chance) {
							generated = true;
							BlockState setState = level.getFluidState(mutable.immutable().above()).is(FluidTags.WATER) ? config.underWaterState().getState(random, mutable) : config.state().getState(random, mutable);
							level.setBlock(mutable, setState, 3);
						}
					}
				} else {
					for (int y = by - config.radius(); y <= by + config.radius(); y++) {
						double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
						if (distance < radiusSquared) {
							mutable.set(x, y, z);
							double sample = EasyNoiseSampler.sample(level, sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
							if (sample > config.minThreshold() && sample < config.maxThreshold() && level.getBlockState(mutable).is(config.replaceableBlocks()) && checkSurroundingBlocks(level, mutable, predicate) && random.nextFloat() <= chance) {
								generated = true;
								BlockState setState = level.getFluidState(mutable.immutable().above()).is(FluidTags.WATER) ? config.underWaterState().getState(random, mutable) : config.state().getState(random, mutable);
								level.setBlock(mutable, setState, 3);
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
