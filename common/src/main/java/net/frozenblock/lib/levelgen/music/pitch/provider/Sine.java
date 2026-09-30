package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Mth;

public class Sine extends WaveProvider {
	public static final MapCodec<Sine> CODEC = createCodec(Sine::new);

	protected Sine(float waveLength) {
		super(waveLength);
	}

	@Override
	protected float sampleFunction(float value) {
		return Mth.sin(value);
	}

	@Override
	public MapCodec<Sine> codec() {
		return CODEC;
	}
}
