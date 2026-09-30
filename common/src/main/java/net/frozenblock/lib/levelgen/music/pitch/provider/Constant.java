package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Constant(float value) implements PitchProvider {
	public static MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("value", 1F).forGetter(Constant::value)
	).apply(instance, Constant::new));

	@Override
	public float sample(long gameTime) {
		return this.value;
	}

	@Override
	public MapCodec<Constant> codec() {
		return CODEC;
	}
}
