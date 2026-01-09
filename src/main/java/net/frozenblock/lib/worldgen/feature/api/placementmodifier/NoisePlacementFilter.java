/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public class NoisePlacementFilter extends PlacementFilter {
	public static final MapCodec<NoisePlacementFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EasyNoiseSampler.NoiseType.CODEC.fieldOf("noise_type").forGetter(config -> config.noiseType),
		Codec.doubleRange(0.0001D, 128D).fieldOf("noise_scale").orElse(0.05).forGetter(config -> config.noiseScale),
		Codec.doubleRange(-1D, 1D).fieldOf("min_threshold").orElse(0.2).forGetter(config -> config.minThreshold),
		Codec.doubleRange(-1D, 1D).fieldOf("maxThresh").orElse(1D).forGetter(config -> config.maxThreshold),
		Codec.doubleRange(0D, 1D).fieldOf("fade_distance").orElse(0D).forGetter(config -> config.fadeDistance),
		Codec.BOOL.fieldOf("use_y").orElse(false).forGetter(config -> config.useY),
		Codec.BOOL.fieldOf("scale_y").orElse(false).forGetter(config -> config.scaleY),
		Codec.BOOL.fieldOf("must_be_inside").orElse(false).forGetter(config -> config.mustBeInside)
	).apply(instance, NoisePlacementFilter::new));

	private final EasyNoiseSampler.NoiseType noiseType;
	private final double noiseScale;
	private final double minThreshold;
	private final double minFadeThreshold;
	private final double maxThreshold;
	private final double maxFadeThreshold;
	private final double fadeDistance;
	private final boolean useY;
	private final boolean scaleY;
	private final boolean mustBeInside;

	public NoisePlacementFilter(
		EasyNoiseSampler.NoiseType noiseType,
		double noiseScale,
		double minThreshold,
		double maxThreshold,
		double fadeDistance,
		boolean useY,
		boolean scaleY,
		boolean mustBeInside
	) {
		this.noiseType = noiseType;
		this.noiseScale = noiseScale;
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThreshold;
		this.fadeDistance = fadeDistance;
		this.minFadeThreshold = minThreshold - fadeDistance;
		this.maxFadeThreshold = maxThreshold + fadeDistance;
		this.useY = useY;
		this.scaleY = scaleY;
		this.mustBeInside = mustBeInside;
		if (this.minThreshold >= this.maxThreshold) throw new IllegalArgumentException("minThresh cannot be greater than or equal to maxThreshold!");
		if (this.fadeDistance < 0) throw new IllegalArgumentException("fadeDistance cannot be less than 0!");
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		final WorldGenLevel level = context.level;
		final ImprovedNoise sampler = this.noiseType.createNoise(level.getSeed());
		final double sample = EasyNoiseSampler.sample(sampler, pos, this.noiseScale, this.scaleY, this.useY);

		boolean isInside = false;
		if (sample > this.minThreshold && sample < this.maxThreshold) isInside = true;
		if (this.fadeDistance > 0) {
			if (sample > this.minFadeThreshold && sample < this.minThreshold) {
				isInside = random.nextDouble() > Math.abs((this.minThreshold - sample) / this.fadeDistance);
			}
			if (sample < this.maxFadeThreshold && sample > this.maxThreshold) {
				isInside = random.nextDouble() > Math.abs((this.maxThreshold - sample) / this.fadeDistance);
			}
		}
		return this.mustBeInside == isInside;
	}

	@Override
	public PlacementModifierType<?> type() {
		return FrozenPlacementModifiers.NOISE_FILTER;
	}
}
