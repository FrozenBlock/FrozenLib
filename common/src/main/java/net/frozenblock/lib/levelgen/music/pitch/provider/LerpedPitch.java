package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record LerpedPitch(PitchProvider provider, PitchProvider start, PitchProvider end) implements PitchProvider {
	public static MapCodec<LerpedPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DIRECT_CODEC.fieldOf("provider").forGetter(LerpedPitch::provider),
		DIRECT_CODEC.fieldOf("start").forGetter(LerpedPitch::start),
		DIRECT_CODEC.fieldOf("end").forGetter(LerpedPitch::end)
	).apply(instance, LerpedPitch::new));

	@Override
	public float sample(long gameTime) {
		return Mth.lerp(this.provider.sample(gameTime), this.start.sample(gameTime), this.end.sample(gameTime));
	}

	@Override
	public boolean applicable(Context context) {
		return this.provider.applicable(context) && this.start.applicable(context) && this.end.applicable(context);
	}

	@Override
	public MapCodec<LerpedPitch> codec() {
		return CODEC;
	}
}
