package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Mth;

public class Cosine extends WaveProvider {
	public static final MapCodec<Cosine> CODEC = createCodec(Cosine::new);

	protected Cosine(float waveLength) {
		super(waveLength);
	}

	@Override
	protected float sampleFunction(float value) {
		return Mth.cos(value);
	}

	@Override
	public MapCodec<Cosine> codec() {
		return CODEC;
	}
}
