package net.frozenblock.lib.worldgen.feature.api.placementmodifier;

import com.mojang.serialization.Codec;
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
	public static final Codec<NoisePlacementFilter> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		Codec.intRange(1, 4).fieldOf("noise").orElse(4).forGetter((config) -> config.noiseIndex),
		Codec.doubleRange(0.0001, 128).fieldOf("multiplier").orElse(0.05).forGetter((config) -> config.multiplier),
		Codec.doubleRange(-1, 1).fieldOf("minThresh").orElse(0.2).forGetter((config) -> config.minThresh),
		Codec.doubleRange(-1, 1).fieldOf("maxThresh").orElse(1D).forGetter((config) -> config.maxThresh),
		Codec.doubleRange(0, 1).fieldOf("fadeDist").orElse(0D).forGetter((config) -> config.fadeDist),
		Codec.BOOL.fieldOf("useY").orElse(false).forGetter((config) -> config.useY),
		Codec.BOOL.fieldOf("multiplyY").orElse(false).forGetter((config) -> config.multiplyY),
		Codec.BOOL.fieldOf("inside").orElse(false).forGetter((config) -> config.inside)
		).apply(instance, NoisePlacementFilter::new));

	private final int noiseIndex;
	private final double multiplier;
	private final double minThresh;
	private final double minFadeThresh;
	private final double maxThresh;
	private final double maxFadeThresh;
	private final double fadeDist;
	private final boolean useY;
	private final boolean multiplyY;
	private final boolean inside;

	public NoisePlacementFilter(int noiseIndex, double multiplier, double minThresh, double maxThresh, double fadeDist, boolean useY, boolean multiplyY, boolean inside) {
		this.noiseIndex = noiseIndex;
		this.multiplier = multiplier;
		this.minThresh = minThresh;
		this.maxThresh = maxThresh;
		this.fadeDist = fadeDist;
		this.minFadeThresh = minThresh - fadeDist;
		this.maxFadeThresh = maxThresh + fadeDist;
		this.useY = useY;
		this.multiplyY = multiplyY;
		this.inside = inside;
		if (this.minThresh >= this.maxThresh) {
			throw new IllegalArgumentException("NoisePlacementFilter minThresh cannot be greater than or equal to maxThresh!");
		}
		if (this.fadeDist < 0) {
			throw new IllegalArgumentException("NoisePlacementFilter fadeDist cannot be less than 0!");
		}
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		WorldGenLevel level = context.level;
		boolean isInside = false;
		ImprovedNoise sampler = this.noiseIndex == 1 ? EasyNoiseSampler.perlinLocal : this.noiseIndex == 2 ? EasyNoiseSampler.perlinChecked : this.noiseIndex == 3 ? EasyNoiseSampler.perlinThreadSafe : EasyNoiseSampler.perlinXoro;
		double sample = EasyNoiseSampler.sample(level, sampler, pos, this.multiplier, this.multiplyY, this.useY);
		if (sample > this.minThresh && sample < this.maxThresh) {
			isInside = true;
		}
		if (this.fadeDist > 0) {
			if (sample > this.minFadeThresh && sample < this.minThresh) {
				isInside = random.nextDouble() > Math.abs((this.minThresh - sample) / this.fadeDist);
			}
			if (sample < this.maxFadeThresh && sample > this.maxThresh) {
				isInside = random.nextDouble() > Math.abs((this.maxThresh - sample) / this.fadeDist);
			}
		}
		return this.inside == isInside;
	}

	@Override
	public PlacementModifierType<?> type() {
		return FrozenPlacementModifiers.NOISE_FILTER;
	}
}
