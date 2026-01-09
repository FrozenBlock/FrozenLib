/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.feature.noise_path;

import com.mojang.serialization.Codec;
import java.util.List;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config.NoiseBandBlockPlacement;
import net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config.NoiseBandPlacement;
import net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config.NoisePathFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public class NoisePathFeature extends Feature<NoisePathFeatureConfig> {

	public NoisePathFeature(Codec<NoisePathFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoisePathFeatureConfig> context) {
		final NoisePathFeatureConfig config = context.config();
		final BlockPos pos = context.origin();
		final WorldGenLevel level = context.level();
		final RandomSource random = level.getRandom();

		final int radius = config.placementRadius();
		final NoiseBandPlacement noiseBandPlacement = config.noiseBandPlacement();

		final ImprovedNoise sampler = noiseBandPlacement.noiseType().createNoise(level.getSeed());
		final double noiseScale = noiseBandPlacement.noiseScale();
		final boolean calculateNoiseWithY = noiseBandPlacement.calculateNoiseWithY();
		final boolean scaleYNoise = noiseBandPlacement.scaleYNoise();

		final List<NoiseBandBlockPlacement> blockPlacements = noiseBandPlacement.blockPlacements();
		final Heightmap.Types heightmapType = noiseBandPlacement.heightmapType().orElse(null);
		final boolean missingHeightmap = heightmapType == null;

		final BlockPos.MutableBlockPos mutable = pos.mutable();
		final int startX = pos.getX();
		final int startY = pos.getY();
		final int startZ = pos.getZ();

		boolean generated = false;
		for (int x = startX - radius; x <= startX + radius; x++) {
			for (int z = startZ - radius; z <= startZ + radius; z++) {
				if (!missingHeightmap) {
					mutable.set(x, level.getHeight(heightmapType, x, z) - 1, z);
					if (AdvancedMath.distanceBetween(pos, mutable, false) < radius) {
						generated = this.attemptPlaceForAllBlockPlacements(
							level,
							mutable,
							random,
							blockPlacements,
							sampler,
							noiseScale,
							calculateNoiseWithY,
							scaleYNoise
						) || generated;
					}
				} else {
					for (int y = startY - radius; y <= startY + radius; y++) {
						mutable.set(x, y, z);
						if (AdvancedMath.distanceBetween(pos, mutable, true) < radius) {
							generated = this.attemptPlaceForAllBlockPlacements(
								level,
								mutable,
								random,
								blockPlacements,
								sampler,
								noiseScale,
								calculateNoiseWithY,
								scaleYNoise
							) || generated;
						}
					}
				}
			}
		}
		return generated;
	}

	public boolean attemptPlaceForAllBlockPlacements(
		WorldGenLevel level,
		BlockPos.MutableBlockPos pos,
		RandomSource random,
		List<NoiseBandBlockPlacement> blockPlacements,
		ImprovedNoise sampler,
		double noiseScale,
		boolean calculateNoiseWithY,
		boolean scaleYNoise
	) {
		double sampleOutput = EasyNoiseSampler.sample(sampler, pos, noiseScale, scaleYNoise, calculateNoiseWithY);
		for (NoiseBandBlockPlacement blockPlacement : blockPlacements) {
			if (blockPlacement.generate(level, pos, random, sampleOutput)) return true;
		}
		return false;
	}

}
