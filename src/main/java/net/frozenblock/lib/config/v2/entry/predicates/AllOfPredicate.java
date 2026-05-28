package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import java.util.List;

public class AllOfPredicate extends CombiningPredicate {
	public static final MapCodec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

	public AllOfPredicate(List<ConfigPredicate> predicates) {
		super(predicates);
	}

	@Override
	public Boolean get() {
		for (ConfigPredicate predicate : this.predicates) {
			if (!predicate.get()) return false;
		}
		return true;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.ALL_OF;
	}


}
