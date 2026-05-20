package net.frozenblock.lib.config.v2.entry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.util.StringRepresentable;

/**
 * A serializable predicate that compares a value with a {@link ConfigEntry}'s value.
 */
public class ConfigEntryPredicate<T> {
	public static final MapCodec<ConfigEntryPredicate<?>> CODEC = ID.CODEC.fieldOf("entry").dispatchMap(
		configEntryPredicate -> configEntryPredicate.id,
		id -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			instance.point(id),
			Operator.CODEC.fieldOf("operator").forGetter(predicate -> predicate.operator),
			((Codec<Object>) ConfigV2Registry.getEntry(id).codec()).fieldOf("target").forGetter(predicate -> predicate.target)
		).apply(instance, ConfigEntryPredicate::new))
	);
	private final ID id;
	private final ConfigEntry<T> entry;
	private final Operator operator;
	private final T target;

	/**
	 * @param id The target {@link ConfigEntry}'s {@link ID}.
	 * @param operator The {@link Operator} used to compare the target {@link ConfigEntry}'s value with.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 */
	public ConfigEntryPredicate(ID id, Operator operator, T target) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
		this.operator = operator;
		this.target = target;

		if (operator.requiresComparable() && !(target instanceof Comparable<?>)) {
			throw new IllegalStateException("Config entry predicate for entry " + id + " is using operator " + operator.getSerializedName() + "without a comparable value!");
		}
	}

	public boolean evaluate() {
		return this.operator.apply(this.target, this.entry.get());
	}

	public enum Operator implements StringRepresentable {
		EQUAL_TO("equal_to", false, (a, b) -> a == b),
		NOT_EQUAL_TO("not_equal_to", false, (a, b) -> a != b),
		GREATER_THAN("greater_than", true, (a, b) -> ((Comparable)a).compareTo(b) > 0),
		LESS_THAN("less_than", true, (a, b) -> ((Comparable)a).compareTo(b) < 0);
		public static final Codec<Operator> CODEC = StringRepresentable.fromEnum(Operator::values);
		private final String name;
		private final boolean requiresComparable;
		private final BiFunction<Object, Object, Boolean> operation;

		Operator(String name, boolean requiresComparable, BiFunction<Object, Object, Boolean> operation) {
			this.name = name;
			this.requiresComparable = requiresComparable;
			this.operation = operation;
		}

		public boolean requiresComparable() {
			return this.requiresComparable;
		}

		public boolean apply(Object value, Object other) {
			return this.operation.apply(value, other);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
