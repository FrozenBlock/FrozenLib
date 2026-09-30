package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;

public class Multiply extends OperationProvider {
	public static final MapCodec<Multiply> CODEC = createCodec(Multiply::new);

	protected Multiply(PitchProvider provider, PitchProvider operand) {
		super(provider, operand);
	}

	@Override
	protected float applyOperation(float sample, float operand) {
		return sample * operand;
	}

	@Override
	public MapCodec<Multiply> codec() {
		return CODEC;
	}
}
