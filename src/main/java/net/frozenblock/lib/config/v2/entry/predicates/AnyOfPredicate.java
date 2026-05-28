package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import java.util.List;

public class AnyOfPredicate extends CombiningPredicate {
	public static final MapCodec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

	public AnyOfPredicate(List<ConfigPredicate> predicates) {
		super(predicates);
	}

	@Override
	public Boolean get() {
		for (ConfigPredicate predicate : this.predicates) {
			if (predicate.get()) return true;
		}
		return false;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.ANY_OF;
	}


}
