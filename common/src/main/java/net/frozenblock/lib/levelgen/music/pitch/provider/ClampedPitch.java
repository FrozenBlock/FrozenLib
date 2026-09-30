package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record ClampedPitch(PitchProvider provider, PitchProvider min, PitchProvider max) implements PitchProvider {
	public static MapCodec<ClampedPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DIRECT_CODEC.fieldOf("provider").forGetter(ClampedPitch::provider),
		DIRECT_CODEC.fieldOf("min").forGetter(ClampedPitch::min),
		DIRECT_CODEC.fieldOf("max").forGetter(ClampedPitch::max)
	).apply(instance, ClampedPitch::new));

	@Override
	public float sample(long gameTime) {
		return Mth.clamp(this.provider.sample(gameTime), this.min.sample(gameTime), this.max.sample(gameTime));
	}

	@Override
	public MapCodec<ClampedPitch> codec() {
		return CODEC;
	}
}
