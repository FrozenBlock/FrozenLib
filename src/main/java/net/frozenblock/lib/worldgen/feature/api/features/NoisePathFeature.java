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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatureUtils;
import net.frozenblock.lib.worldgen.feature.api.features.config.PathFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jetbrains.annotations.NotNull;

public class NoisePathFeature extends Feature<PathFeatureConfig> {

	public NoisePathFeature(Codec<PathFeatureConfig> codec) {
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
		ImprovedNoise sampler = config.noiseType().createNoise(level.getSeed());
		float chance = config.placementChance();
		int bx = blockPos.getX();
		int by = blockPos.getY();
		int bz = blockPos.getZ();
		BlockPos.MutableBlockPos mutable = blockPos.mutable();

		for (int x = bx - config.radius(); x <= bx + config.radius(); x++) {
			for (int z = bz - config.radius(); z <= bz + config.radius(); z++) {
				if (!config.is3D()) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radiusSquared) {
						mutable.set(x, level.getHeight(Types.OCEAN_FLOOR, x, z) - 1, z);
						double sample = EasyNoiseSampler.sample(sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
						generated = this.attemptPlaceBlock(config, level, random, chance, mutable, sample) || generated;
					}
				} else {
					for (int y = by - config.radius(); y <= by + config.radius(); y++) {
						double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
						if (distance < radiusSquared) {
							mutable.set(x, y, z);
							double sample = EasyNoiseSampler.sample(sampler, mutable, config.noiseScale(), config.scaleY(), config.useY());
							generated = this.attemptPlaceBlock(config, level, random, chance, mutable, sample) || generated;
						}
					}
				}
			}
		}
		return generated;
	}

	public boolean attemptPlaceBlock(
		@NotNull PathFeatureConfig config,
		WorldGenLevel level,
		RandomSource random,
		float chance,
		BlockPos.MutableBlockPos mutable,
		double sample
	) {
		if (sample > config.minThreshold()
			&& sample < config.maxThreshold()
			&& level.getBlockState(mutable).is(config.replaceableBlocks())
			&& (!config.onlyPlaceWhenExposed() || FrozenLibFeatureUtils.isBlockExposed(level, mutable))
			&& random.nextFloat() <= chance
		) {
			return this.placeBlock(level, config.state().getState(random, mutable), mutable);
		}
		return false;
	}

	public boolean placeBlock(@NotNull WorldGenLevel level, BlockState state, BlockPos pos) {
		level.setBlock(pos, state, Block.UPDATE_CLIENTS);
		return true;
	}

}
