package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import java.util.List;

public class AllMatchPredicate extends CombiningPredicate {
	public static final MapCodec<AllMatchPredicate> CODEC = codec(AllMatchPredicate::new);

	public AllMatchPredicate(List<ConfigPredicate> predicates) {
		super(predicates);
	}

	@Override
	public Boolean get() {
		Boolean last = null;
		for (ConfigPredicate predicate : this.predicates) {
			final boolean predicateValue = predicate.get();
			last = last != null ? last : predicateValue;
			if (predicateValue != last) return false;
		}
		return true;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.ALL_MATCH;
	}


}
