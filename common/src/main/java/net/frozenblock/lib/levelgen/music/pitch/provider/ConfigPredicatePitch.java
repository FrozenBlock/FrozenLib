package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;

public record ConfigPredicatePitch(Holder<ConfigPredicate> predicate, PitchProvider provider) implements PitchProvider {
	public static MapCodec<ConfigPredicatePitch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.HOLDER_CODEC.fieldOf("predicate").forGetter(ConfigPredicatePitch::predicate),
		DIRECT_CODEC.fieldOf("provider").forGetter(ConfigPredicatePitch::provider)
	).apply(instance, ConfigPredicatePitch::new));

	@Override
	public float sample(long gameTime) {
		return this.provider.sample(gameTime);
	}

	@Override
	public boolean applicable(Context context) {
		return this.predicate.value().test() && this.provider.applicable(context);
	}

	@Override
	public MapCodec<ConfigPredicatePitch> codec() {
		return CODEC;
	}
}
