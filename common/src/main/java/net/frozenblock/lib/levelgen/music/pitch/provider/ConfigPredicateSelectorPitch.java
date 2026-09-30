package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;

public record ConfigPredicateSelectorPitch(Holder<ConfigPredicate> predicate, PitchProvider whenTrue, PitchProvider whenFalse) implements PitchProvider {
	public static MapCodec<ConfigPredicateSelectorPitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("selector").forGetter(ConfigPredicateSelectorPitch::predicate),
		DIRECT_CODEC.fieldOf("when_true").forGetter(ConfigPredicateSelectorPitch::whenTrue),
		DIRECT_CODEC.fieldOf("when_false").forGetter(ConfigPredicateSelectorPitch::whenFalse)
	).apply(instance, ConfigPredicateSelectorPitch::new));

	@Override
	public float sample(long gameTime) {
		return (this.predicate.value().test() ? this.whenTrue : this.whenFalse).sample(gameTime);
	}

	@Override
	public MapCodec<ConfigPredicateSelectorPitch> codec() {
		return CODEC;
	}
}
