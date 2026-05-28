package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

public abstract class CombiningPredicate implements ConfigPredicate {
	protected final List<ConfigPredicate> predicates;

	protected CombiningPredicate(List<ConfigPredicate> predicates) {
		this.predicates = predicates;
	}

	public static <T extends CombiningPredicate> MapCodec<T> codec(Function<List<ConfigPredicate>, T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConfigPredicate.CODEC.listOf().fieldOf("predicates").forGetter(predicate -> predicate.predicates)
		).apply(instance, constructor));
	}
}
