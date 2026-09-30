package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.Mth;

public abstract class WaveProvider implements PitchProvider {
	protected final float waveLength;
	private final float timeToFunctionValue;

	protected WaveProvider(float waveLength) {
		this.waveLength = waveLength;
		this.timeToFunctionValue = Mth.TWO_PI / this.waveLength;
	}

	@Override
	public final float sample(long gameTime) {
		return this.sampleFunction(gameTime * this.timeToFunctionValue);
	}

	protected abstract float sampleFunction(float value);

	protected static <T extends WaveProvider> MapCodec<T> createCodec(Function<Float, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("wave_length").forGetter(provider -> provider.waveLength)
		).apply(instance, constructor));
	}
}
