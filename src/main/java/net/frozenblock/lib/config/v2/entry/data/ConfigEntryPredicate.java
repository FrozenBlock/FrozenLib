/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.v2.entry.data;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.ConfigEntryBlockPredicate;
import net.frozenblock.lib.levelgen.placement.api.ConfigEntryPlacementFilter;
import net.frozenblock.lib.loot.api.predicates.ConfigEntryCondition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.ApiStatus;

/**
 * A serializable predicate that compares a value with a {@link ConfigEntry}'s value.
 */
public class ConfigEntryPredicate<T> {
	private static final ConcurrentMap<InternKey, ConfigEntryPredicate<?>> VALUES = new MapMaker().weakValues().makeMap();
	public static final MapCodec<ConfigEntryPredicate<?>> MAP_CODEC = ID.CODEC.fieldOf("entry").dispatchMap(
		configEntryPredicate -> configEntryPredicate.id,
		id -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			instance.point(id),
			Operator.CODEC.fieldOf("operator").forGetter(predicate -> predicate.operator),
			((Codec<Object>) ConfigV2Registry.getEntry(id).codec()).fieldOf("target").forGetter(predicate -> predicate.target),
			Inlined.CODEC.optionalFieldOf("with").forGetter(predicate -> predicate.inlinedPredicate)
		).apply(instance, ConfigEntryPredicate::create))
	);
	public static final Codec<ConfigEntryPredicate<?>> CODEC = MAP_CODEC.codec();
	private final ID id;
	private final ConfigEntry<T> entry;
	private final Operator operator;
	private final T target;
	private final Optional<Inlined> inlinedPredicate;
	private ConfigEntryBlockPredicate blockPredicate;
	private ConfigEntryPlacementFilter<T> placementFilter;
	private ConfigEntryCondition lootCondition;

	/**
	 * @param id The target {@link ConfigEntry}'s {@link ID}.
	 * @param operator The {@link Operator} used to compare the target {@link ConfigEntry}'s value with.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @param inlinedPredicate Another {@link ConfigEntryPredicate} to evaluate, wrapped as an {@link Inlined}.
	 */
	private ConfigEntryPredicate(ID id, Operator operator, T target, Optional<Inlined> inlinedPredicate) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
		this.operator = operator;
		this.target = target;
		this.inlinedPredicate = inlinedPredicate;

		if (operator.requiresComparable() && !(target instanceof Comparable<?>)) {
			throw new IllegalStateException("Config entry predicate for entry " + id + " is using operator " + operator.getSerializedName() + "without a comparable value!");
		}
	}

	/**
	 * @param id The target {@link ConfigEntry}'s {@link ID}.
	 * @param operator The {@link Operator} used to compare the target {@link ConfigEntry}'s value with.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 */
	public static ConfigEntryPredicate<?> create(ID id, Operator operator, Object target) {
		return VALUES.computeIfAbsent(
			new InternKey(id, operator, target, Optional.empty()),
			key -> new ConfigEntryPredicate<>(key.id, key.operator, key.target, key.with)
		);
	}

	/**
	 * @param id The target {@link ConfigEntry}'s {@link ID}.
	 * @param operator The {@link Operator} used to compare the target {@link ConfigEntry}'s value with.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @param inlinedPredicate Another {@link ConfigEntryPredicate} to evaluate, wrapped as an {@link Inlined}.
	 */
	public static ConfigEntryPredicate<?> create(ID id, Operator operator, Object target, Optional<Inlined> inlinedPredicate) {
		return VALUES.computeIfAbsent(
			new InternKey(id, operator, target, inlinedPredicate),
			key -> new ConfigEntryPredicate<>(key.id, key.operator, key.target, key.with)
		);
	}

	/**
	 * @param entry The target {@link ConfigEntry}.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if the entry and target values are equal.
	 */
	public static <T> ConfigEntryPredicate<T> equalTo(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.EQUAL_TO, target);
	}

	/**
	 * @param entry The target {@link ConfigEntry}.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if the entry and target values are not equal.
	 */
	public static <T> ConfigEntryPredicate<T> notEqualTo(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.NOT_EQUAL_TO, target);
	}

	/**
	 * @param entry The target {@link ConfigEntry}.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if the entry value is greater than the target value.
	 */
	public static <T> ConfigEntryPredicate<T> greaterThan(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.GREATER_THAN, target);
	}

	/**
	 * @param entry The target {@link ConfigEntry}.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if the entry value is less than the target value.
	 */
	public static <T> ConfigEntryPredicate<T> lessThan(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.LESS_THAN, target);
	}

	/**
	 * @param configEntryPredicate Another {@link ConfigEntryPredicate} to be evaluated.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if both predicates return the same value.
	 */
	public ConfigEntryPredicate<?> equalTo(ConfigEntryPredicate<?> configEntryPredicate) {
		return create(this.id, this.operator, this.target, Optional.of(new Inlined(Inlined.InlinedOperator.EQUAL_TO, configEntryPredicate)));
	}

	/**
	 * @param configEntryPredicate Another {@link ConfigEntryPredicate} to be evaluated.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if both predicates return different values.
	 */
	public ConfigEntryPredicate<?> notEqualTo(ConfigEntryPredicate<?> configEntryPredicate) {
		return create(this.id, this.operator, this.target, Optional.of(new Inlined(Inlined.InlinedOperator.NOT_EQUAL_TO, configEntryPredicate)));
	}

	/**
	 * @param configEntryPredicate Another {@link ConfigEntryPredicate} to be evaluated.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if both predicates return {@code true}.
	 */
	public ConfigEntryPredicate<?> and(ConfigEntryPredicate<?> configEntryPredicate) {
		return create(this.id, this.operator, this.target, Optional.of(new Inlined(Inlined.InlinedOperator.AND, configEntryPredicate)));
	}

	/**
	 * @param configEntryPredicate Another {@link ConfigEntryPredicate} to be evaluated.
	 * @return A {@link ConfigEntryPredicate} that returns {@code true} if either predicate returns {@code true}.
	 */
	public ConfigEntryPredicate<?> or(ConfigEntryPredicate<?> configEntryPredicate) {
		return create(this.id, this.operator, this.target, Optional.of(new Inlined(Inlined.InlinedOperator.OR, configEntryPredicate)));
	}

	/**
	 * @return The {@link ConfigEntryPredicate} wrapped in {@link BlockPredicate} form.
	 */
	public ConfigEntryBlockPredicate asBlockPredicate() {
		if (this.blockPredicate == null) this.blockPredicate = new ConfigEntryBlockPredicate(this);
		return this.blockPredicate;
	}

	/**
	 * @return The {@link ConfigEntryPredicate} wrapped in {@link BlockPredicateFilter} form.
	 */
	public BlockPredicateFilter asBlockPredicateFilter() {
		return BlockPredicateFilter.forPredicate(this.blockPredicate);
	}

	/**
	 * @return The {@link ConfigEntryPredicate} wrapped in {@link PlacementFilter} form.
	 */
	public ConfigEntryPlacementFilter<T> asPlacementFilter() {
		if (this.placementFilter == null) this.placementFilter = new ConfigEntryPlacementFilter<>(this);
		return this.placementFilter;
	}

	/**
	 * @return The {@link ConfigEntryPredicate} wrapped in {@link LootItemCondition} form.
	 */
	public ConfigEntryCondition asLootCondition() {
		if (this.lootCondition == null) this.lootCondition = new ConfigEntryCondition(this);
		return this.lootCondition;
	}

	/**
	 * @return Whether this predicate is currently true.
	 */
	public boolean test() {
		final boolean testMe = this.operator.apply(this.entry, this.target);
		return this.inlinedPredicate.map(with -> with.test(testMe)).orElse(testMe);
	}

	/**
	 * Compares a {@link ConfigEntryPredicate}'s output with another {@link ConfigEntryBlockPredicate}'s output, and returns {@code true} if the condition is met.
	 */
	public record Inlined(InlinedOperator operator, ConfigEntryPredicate<?> configEntryPredicate) {
		public static final Codec<Inlined> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			InlinedOperator.CODEC.fieldOf("operator").forGetter(inlined -> inlined.operator),
			ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(inlined -> inlined.configEntryPredicate)
		).apply(instance, Inlined::new));

		public boolean test(boolean original) {
			return this.operator.apply(original, this.configEntryPredicate.test());
		}

		/**
		 * Compares two {@link Boolean}s, and returns {@code true} if the condition is met.
		 */
		public enum InlinedOperator implements StringRepresentable {
			/**
			 * Returns {@code true} if both {@link ConfigEntryPredicate}s return the same value.
			 */
			EQUAL_TO("equal_to", (a, b) -> a == b),
			/**
			 * Returns {@code true} if both {@link ConfigEntryPredicate}s return different values.
			 */
			NOT_EQUAL_TO("not_equal_to", (a, b) -> a != b),
			/**
			 * Returns {@code true} if both {@link ConfigEntryPredicate}s return {@code true}.
			 */
			AND("and", (a, b) -> a && b),
			/**
			 * Returns {@code true} if either {@link ConfigEntryPredicate} returns {@code true}.
			 */
			OR("or", (a, b) -> a || b);
			public static final Codec<InlinedOperator> CODEC = StringRepresentable.fromEnum(InlinedOperator::values);
			private final String name;
			private final BiFunction<Boolean, Boolean, Boolean> operation;

			InlinedOperator(String name, BiFunction<Boolean, Boolean, Boolean> operation) {
				this.name = name;
				this.operation = operation;
			}

			public boolean apply(Boolean value, Boolean other) {
				return this.operation.apply(value, other);
			}

			@Override
			public String getSerializedName() {
				return this.name;
			}
		}
	}

	/**
	 * Compares a {@link ConfigEntry}'s value with a target value, and returns {@code true} if the condition is met.
	 */
	public enum Operator implements StringRepresentable {
		/**
		 * Returns {@code true} if the {@link ConfigEntry}'s value matches the target value.
		 */
		EQUAL_TO("equal_to", false, (a, b) -> a == b),
		/**
		 * Returns {@code true} if the {@link ConfigEntry}'s value does not match the target value.
		 */
		NOT_EQUAL_TO("not_equal_to", false, (a, b) -> a != b),
		/**
		 * Returns {@code true} if the {@link ConfigEntry}'s value is greater than the target value.
		 */
		GREATER_THAN("greater_than", true, (a, b) -> ((Comparable)a).compareTo(b) > 0),
		/**
		 * Returns {@code true} if the {@link ConfigEntry}'s value is less than the target value.
		 */
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

		public boolean apply(ConfigEntry<?> entry, Object other) {
			return this.operation.apply(entry.get(), other);
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}

	@ApiStatus.Internal
	private record InternKey(ID id, Operator operator, Object target, Optional<Inlined> with) {}
}
