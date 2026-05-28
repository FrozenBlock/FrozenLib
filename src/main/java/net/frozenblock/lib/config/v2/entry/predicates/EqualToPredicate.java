package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.config.v2.registry.ID;

public class EqualToPredicate<T> extends ValuePredicate<T> {
	public static final MapCodec<EqualToPredicate<?>> CODEC = codec(EqualToPredicate::new);

	public EqualToPredicate(ID id, T target) {
		super(id, target);
	}

	@Override
	public Boolean get() {
		return this.entry.get() == this.target;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.EQUAL_TO;
	}
}
