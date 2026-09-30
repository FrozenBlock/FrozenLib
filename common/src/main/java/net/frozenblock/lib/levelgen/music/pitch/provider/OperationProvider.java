package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;

public abstract class OperationProvider implements PitchProvider {
	protected final PitchProvider provider;
	protected final PitchProvider operand;

	protected OperationProvider(PitchProvider provider, PitchProvider operand) {
		this.provider = provider;
		this.operand = operand;
	}

	@Override
	public final float sample(long gameTime) {
		return this.applyOperation(this.provider.sample(gameTime), this.operand.sample(gameTime));
	}

	protected abstract float applyOperation(float sample, float operand);

	@Override
	public boolean applicable(Context context) {
		return this.provider.applicable(context) && this.operand.applicable(context);
	}

	protected static <T extends OperationProvider> MapCodec<T> createCodec(BiFunction<PitchProvider, PitchProvider, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			PitchProvider.DIRECT_CODEC.fieldOf("provider").forGetter(provider -> provider.provider),
			PitchProvider.DIRECT_CODEC.fieldOf("operand").forGetter(provider -> provider.operand)
		).apply(instance, constructor::apply));
	}
}
