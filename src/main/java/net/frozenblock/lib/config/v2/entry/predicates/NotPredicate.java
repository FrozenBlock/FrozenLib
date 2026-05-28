package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NotPredicate implements ConfigPredicate {
	public static final MapCodec<NotPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("predicate").forGetter(predicate -> predicate.predicate)
	).apply(instance, NotPredicate::new));
	private final ConfigPredicate predicate;

	public NotPredicate(ConfigPredicate predicate) {
		this.predicate = predicate;
	}

	@Override
	public Boolean get() {
		return !this.predicate.get();
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.NOT;
	}
}
