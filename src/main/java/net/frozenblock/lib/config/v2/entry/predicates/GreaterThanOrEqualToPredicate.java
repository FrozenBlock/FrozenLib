package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.config.v2.registry.ID;

public class GreaterThanOrEqualToPredicate<T> extends ValuePredicate<T> {
	public static final MapCodec<GreaterThanOrEqualToPredicate<?>> CODEC = codec(GreaterThanOrEqualToPredicate::new);

	public GreaterThanOrEqualToPredicate(ID id, T target) {
		super(id, target);
	}

	@Override
	public Boolean get() {
		return ((Comparable) this.entry.get()).compareTo(this.target) >= 0;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.GREATER_THAN_OR_EQUAL_TO;
	}
}
