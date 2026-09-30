package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;

public class Divide extends OperationProvider {
	public static final MapCodec<Divide> CODEC = createCodec(Divide::new);

	protected Divide(PitchProvider provider, PitchProvider operand) {
		super(provider, operand);
	}

	@Override
	protected float applyOperation(float sample, float operand) {
		return sample / operand;
	}

	@Override
	public MapCodec<Divide> codec() {
		return CODEC;
	}
}
