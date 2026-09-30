package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;

public class Subtract extends OperationProvider {
	public static final MapCodec<Subtract> CODEC = createCodec(Subtract::new);

	protected Subtract(PitchProvider provider, PitchProvider operand) {
		super(provider, operand);
	}

	@Override
	protected float applyOperation(float sample, float operand) {
		return sample - operand;
	}

	@Override
	public MapCodec<Subtract> codec() {
		return CODEC;
	}
}
