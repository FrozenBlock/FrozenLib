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

package net.frozenblock.lib.levelgen.feature.api.feature.noise_path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.levelgen.feature.api.feature.noise_path.config.NoiseBandBlockPlacement;
import net.frozenblock.lib.levelgen.feature.api.feature.noise_path.config.NoiseBandPlacement;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public record NoisePathFeature(
	NoiseBandPlacement noiseBandPlacement,
	int placementRadius
) implements Feature {
	public static final MapCodec<NoisePathFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NoiseBandPlacement.CODEC.fieldOf("noise_band_placement").forGetter(NoisePathFeature::noiseBandPlacement),
		Codec.intRange(1, 16).fieldOf("placement_radius").orElse(10).forGetter(NoisePathFeature::placementRadius)
	).apply(instance, NoisePathFeature::new));

	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final PerlinNoise sampler = this.noiseBandPlacement.noiseType().createNoise(level.getSeed());
		final double noiseScale = this.noiseBandPlacement.noiseScale();
		final boolean calculateNoiseWithY = this.noiseBandPlacement.calculateNoiseWithY();
		final boolean scaleYNoise = this.noiseBandPlacement.scaleYNoise();

		final List<NoiseBandBlockPlacement> blockPlacements = this.noiseBandPlacement.blockPlacements();
		final Heightmap.Types heightmapType = this.noiseBandPlacement.heightmapType().orElse(null);
		final boolean missingHeightmap = heightmapType == null;

		final BlockPos.MutableBlockPos mutable = origin.mutable();
		final int startX = origin.getX();
		final int startY = origin.getY();
		final int startZ = origin.getZ();

		boolean generated = false;
		for (int x = startX - this.placementRadius; x <= startX + this.placementRadius; x++) {
			for (int z = startZ - this.placementRadius; z <= startZ + this.placementRadius; z++) {
				if (!missingHeightmap) {
					mutable.set(x, level.getHeight(heightmapType, x, z) - 1, z);
					if (AdvancedMath.distanceBetween(origin, mutable, false) < this.placementRadius) {
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
					for (int y = startY - this.placementRadius; y <= startY + this.placementRadius; y++) {
						mutable.set(x, y, z);
						if (AdvancedMath.distanceBetween(origin, mutable, true) < this.placementRadius) {
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
		PerlinNoise sampler,
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
