package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;

public class Add extends OperationProvider {
	public static final MapCodec<Add> CODEC = createCodec(Add::new);

	protected Add(PitchProvider provider, PitchProvider operand) {
		super(provider, operand);
	}

	@Override
	protected float applyOperation(float sample, float operand) {
		return sample + operand;
	}

	@Override
	public MapCodec<Add> codec() {
		return CODEC;
	}
}
