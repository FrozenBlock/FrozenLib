package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;

public class TrueConfigPredicate implements ConfigPredicate {
	public static TrueConfigPredicate INSTANCE = new TrueConfigPredicate();
	public static final MapCodec<TrueConfigPredicate> CODEC = MapCodec.unit(() -> INSTANCE);

	private TrueConfigPredicate() {}

	@Override
	public Boolean get() {
		return true;
	}

	@Override
	public ConfigPredicateType<?> type() {
		return ConfigPredicateType.TRUE;
	}
}
