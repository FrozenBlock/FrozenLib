package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;

public interface ConfigPredicateType<P extends ConfigPredicate> {
	ConfigPredicateType<EqualToPredicate<?>> EQUAL_TO = register("equal_to", EqualToPredicate.CODEC);
	ConfigPredicateType<GreaterThanPredicate<?>> GREATER_THAN = register("greater_than", GreaterThanPredicate.CODEC);
	ConfigPredicateType<GreaterThanOrEqualToPredicate<?>> GREATER_THAN_OR_EQUAL_TO = register("greater_than_or_equal_to", GreaterThanOrEqualToPredicate.CODEC);
	ConfigPredicateType<LessThanPredicate<?>> LESS_THAN = register("less_than", LessThanPredicate.CODEC);
	ConfigPredicateType<LessThanOrEqualToPredicate<?>> LESS_THAN_OR_EQUAL_TO = register("less_than_or_equal_to", LessThanOrEqualToPredicate.CODEC);
	ConfigPredicateType<AllMatchPredicate> ALL_MATCH = register("all_match", AllMatchPredicate.CODEC);
	ConfigPredicateType<AnyOfPredicate> ANY_OF = register("any_of", AnyOfPredicate.CODEC);
	ConfigPredicateType<AllOfPredicate> ALL_OF = register("all_of", AllOfPredicate.CODEC);
	ConfigPredicateType<NotPredicate> NOT = register("not", NotPredicate.CODEC);
	ConfigPredicateType<ExistsPredicate> EXISTS = register("exists", ExistsPredicate.CODEC);
	ConfigPredicateType<NotPredicate> TRUE = register("true", NotPredicate.CODEC);

	MapCodec<P> codec();

	static void init() {}

	private static <P extends ConfigPredicate> ConfigPredicateType<P> register(String name, MapCodec<P> codec) {
		return Registry.register(FrozenLibRegistries.CONFIG_PREDICATE_TYPE, name, () -> codec);
	}
}
