/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.world.level.levelgen.Heightmap;

public record NoiseBandPlacement(
	EasyNoiseSampler.NoiseType noiseType,
	double noiseScale,
	boolean calculateNoiseWithY,
	boolean scaleYNoise,
	Optional<Heightmap.Types> heightmapType,
	List<NoiseBandBlockPlacement> blockPlacements
) {
	public static final MapCodec<NoiseBandPlacement> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			EasyNoiseSampler.NoiseType.CODEC.fieldOf("noise_type").forGetter(config -> config.noiseType),
			Codec.DOUBLE.lenientOptionalFieldOf("noise_scale", 1D).forGetter(config -> config.noiseScale),
			Codec.BOOL.lenientOptionalFieldOf("include_y_in_noise_calculation", false).forGetter(config -> config.calculateNoiseWithY),
			Codec.BOOL.lenientOptionalFieldOf("scale_noise_y", false).forGetter(config -> config.scaleYNoise),
			Heightmap.Types.CODEC.lenientOptionalFieldOf("heightmap").forGetter(config -> config.heightmapType),
			NoiseBandBlockPlacement.CODEC.listOf().fieldOf("block_placements").forGetter(config -> config.blockPlacements)
		).apply(instance, NoiseBandPlacement::new)
	);

	public static class Builder {
		private final EasyNoiseSampler.NoiseType noiseType;
		private double noiseScale = 0.2D;
		private boolean calculateNoiseWithY = false;
		private boolean scaleYNoise = false;
		private Optional<Heightmap.Types> heightmapType = Optional.empty();
		private List<NoiseBandBlockPlacement> blockPlacements = new ArrayList<>();

		public Builder(EasyNoiseSampler.NoiseType noiseType) {
			this.noiseType = noiseType;
		}

		public Builder noiseScale(double noiseScale) {
			this.noiseScale = noiseScale;
			return this;
		}

		public Builder calculateNoiseWithY() {
			this.calculateNoiseWithY = true;
			return this;
		}

		public Builder scaleYNoise() {
			this.scaleYNoise = true;
			return this;
		}

		public Builder heightmapType(Heightmap.Types type) {
			this.heightmapType = Optional.ofNullable(type);
			return this;
		}

		public Builder noiseBandBlockPlacement(NoiseBandBlockPlacement blockPlacement) {
			this.blockPlacements = List.of(blockPlacement);
			return this;
		}

		public Builder noiseBandBlockPlacements(NoiseBandBlockPlacement... blockPlacements) {
			this.blockPlacements = Arrays.stream(blockPlacements).toList();
			return this;
		}

		public NoiseBandPlacement build() {
			if (this.blockPlacements.isEmpty()) throw new IllegalStateException("No NoiseBandBlockPlacements found for NoiseBandPlacement!");
			return new NoiseBandPlacement(
				this.noiseType,
				this.noiseScale,
				this.calculateNoiseWithY,
				this.scaleYNoise,
				this.heightmapType,
				this.blockPlacements
			);
		}
	}
}
