package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;

public abstract class ValuePredicate<T> implements ConfigPredicate {
	final ID id;
	final ConfigEntry<T> entry;
	final T target;

	protected ValuePredicate(ID id, T target) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
		this.target = target;
	}

	public static <T extends ValuePredicate<?>> MapCodec<T> codec(BiFunction<ID, Object, T> constructor) {
		return ID.CODEC.fieldOf("entry").dispatchMap(
			predicate -> predicate.id,
			id -> RecordCodecBuilder.mapCodec(instance -> instance.group(
				instance.point(id),
				((Codec<Object>) ConfigV2Registry.getEntry(id).codec()).fieldOf("target").forGetter(predicate -> predicate.target)
			).apply(instance, constructor))
		);
	}
}
