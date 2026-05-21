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
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.ConfigEntryBlockPredicate;
import net.frozenblock.lib.levelgen.placement.api.ConfigEntryPlacementFilter;
import net.frozenblock.lib.loot.api.predicates.ConfigEntryCondition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;

/**
 * A serializable predicate that compares a value with a {@link ConfigEntry}'s value.
 */
public class ConfigEntryPredicate<T> {
	private static final ConcurrentMap<InternKey, ConfigEntryPredicate<?>> VALUES = new MapMaker().weakValues().makeMap();
	public static final MapCodec<ConfigEntryPredicate<?>> CODEC = ID.CODEC.fieldOf("entry").dispatchMap(
		configEntryPredicate -> configEntryPredicate.id,
		id -> RecordCodecBuilder.mapCodec(instance -> instance.group(
			instance.point(id),
			Operator.CODEC.fieldOf("operator").forGetter(predicate -> predicate.operator),
			((Codec<Object>) ConfigV2Registry.getEntry(id).codec()).fieldOf("target").forGetter(predicate -> predicate.target)
		).apply(instance, ConfigEntryPredicate::create))
	);
	private final ID id;
	private final ConfigEntry<T> entry;
	private final Operator operator;
	private final T target;
	private ConfigEntryBlockPredicate blockPredicate;
	private ConfigEntryPlacementFilter placementFilter;
	private ConfigEntryCondition lootCondition;

	/**
	 * @param id The target {@link ConfigEntry}'s {@link ID}.
	 * @param operator The {@link Operator} used to compare the target {@link ConfigEntry}'s value with.
	 * @param target The value the target {@link ConfigEntry}'s value is being compared with.
	 */
	private ConfigEntryPredicate(ID id, Operator operator, T target) {
		this.id = id;
		this.entry = (ConfigEntry<T>) ConfigV2Registry.getEntry(id);
		this.operator = operator;
		this.target = target;

		if (operator.requiresComparable() && !(target instanceof Comparable<?>)) {
			throw new IllegalStateException("Config entry predicate for entry " + id + " is using operator " + operator.getSerializedName() + "without a comparable value!");
		}
	}

	public static ConfigEntryPredicate<?> create(ID id, Operator operator, Object target) {
		return VALUES.computeIfAbsent(
			new InternKey(id, operator, target),
			key -> new ConfigEntryPredicate<>(key.id, key.operator, key.target)
		);
	}

	public static <T> ConfigEntryPredicate<T> equalTo(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.EQUAL_TO, target);
	}

	public static <T> ConfigEntryPredicate<T> notEqualTo(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.NOT_EQUAL_TO, target);
	}

	public static <T> ConfigEntryPredicate<T> greaterThan(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.GREATER_THAN, target);
	}

	public static <T> ConfigEntryPredicate<T> lessThan(ConfigEntry<T> entry, T target) {
		return (ConfigEntryPredicate<T>) create(entry.id(), Operator.LESS_THAN, target);
	}

	public ConfigEntryBlockPredicate asBlockPredicate() {
		if (this.blockPredicate == null) this.blockPredicate = new ConfigEntryBlockPredicate(this);
		return this.blockPredicate;
	}

	public BlockPredicateFilter asBlockPredicateFilter() {
		return BlockPredicateFilter.forPredicate(this.blockPredicate);
	}

	public ConfigEntryPlacementFilter<T> asPlacementFilter() {
		if (this.placementFilter == null) this.placementFilter = new ConfigEntryPlacementFilter<>(this);
		return this.placementFilter;
	}

	public ConfigEntryCondition asLootCondition() {
		if (this.lootCondition == null) this.lootCondition = new ConfigEntryCondition(this);
		return this.lootCondition;
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

	private record InternKey(ID id, Operator operator, Object target) {}
}
