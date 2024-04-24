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
import org.jetbrains.annotations.NotNull;

public class NoisePlacementFilter extends PlacementFilter {
	public static final MapCodec<NoisePlacementFilter> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.intRange(1, 4).fieldOf("noise").orElse(4).forGetter((config) -> config.noise),
		Codec.doubleRange(0.0001, 128).fieldOf("noise_scale").orElse(0.05).forGetter((config) -> config.noiseScale),
		Codec.doubleRange(-1, 1).fieldOf("min_threshold").orElse(0.2).forGetter((config) -> config.minThreshold),
		Codec.doubleRange(-1, 1).fieldOf("maxThresh").orElse(1D).forGetter((config) -> config.maxThreshold),
		Codec.doubleRange(0, 1).fieldOf("fade_distance").orElse(0D).forGetter((config) -> config.fadeDistance),
		Codec.BOOL.fieldOf("use_y").orElse(false).forGetter((config) -> config.useY),
		Codec.BOOL.fieldOf("scale_y").orElse(false).forGetter((config) -> config.scaleY),
		Codec.BOOL.fieldOf("must_be_inside").orElse(false).forGetter((config) -> config.mustBeInside)
		).apply(instance, NoisePlacementFilter::new));

	private final int noise;
	private final double noiseScale;
	private final double minThreshold;
	private final double minFadeThreshold;
	private final double maxThreshold;
	private final double maxFadeThreshold;
	private final double fadeDistance;
	private final boolean useY;
	private final boolean scaleY;
	private final boolean mustBeInside;

	public NoisePlacementFilter(int noise, double noiseScale, double minThreshold, double maxThreshold, double fadeDistance, boolean useY, boolean scaleY, boolean mustBeInside) {
		this.noise = noise;
		this.noiseScale = noiseScale;
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThreshold;
		this.fadeDistance = fadeDistance;
		this.minFadeThreshold = minThreshold - fadeDistance;
		this.maxFadeThreshold = maxThreshold + fadeDistance;
		this.useY = useY;
		this.scaleY = scaleY;
		this.mustBeInside = mustBeInside;
		if (this.minThreshold >= this.maxThreshold) {
			throw new IllegalArgumentException("NoisePlacementFilter minThresh cannot be greater than or equal to maxThreshold!");
		}
		if (this.fadeDistance < 0) {
			throw new IllegalArgumentException("NoisePlacementFilter fadeDistance cannot be less than 0!");
		}
	}

	@Override
	protected boolean shouldPlace(@NotNull PlacementContext context, RandomSource random, BlockPos pos) {
		WorldGenLevel level = context.level;
		boolean isInside = false;
		ImprovedNoise sampler = this.noise == 1 ? EasyNoiseSampler.perlinLocal : this.noise == 2 ? EasyNoiseSampler.perlinChecked : this.noise == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
		double sample = EasyNoiseSampler.sample(level, sampler, pos, this.noiseScale, this.scaleY, this.useY);
		if (sample > this.minThreshold && sample < this.maxThreshold) {
			isInside = true;
		}
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
	@NotNull
	public PlacementModifierType<?> type() {
		return FrozenPlacementModifiers.NOISE_FILTER;
	}
}
