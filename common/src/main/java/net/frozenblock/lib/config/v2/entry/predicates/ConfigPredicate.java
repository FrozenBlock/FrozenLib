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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.entity.api.variant.ConfigCheck;
import net.frozenblock.lib.levelgen.blockpredicates.ConfigBlockPredicate;
import net.frozenblock.lib.levelgen.feature.api.treedecorators.ConfigPredicateDecorator;
import net.frozenblock.lib.levelgen.placement.api.ConfigPlacementFilter;
import net.frozenblock.lib.levelgen.material.impl.ConfigConditionSource;
import net.frozenblock.lib.item.api.loot.predicates.ConfigLootCondition;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConfigPredicate extends Supplier<Boolean> {
	Codec<ConfigPredicate> CODEC = FrozenLibRegistries.CONFIG_PREDICATE_TYPE.byNameCodec().dispatch(ConfigPredicate::codec, Function.identity());

	MapCodec<? extends ConfigPredicate> codec();

	default boolean test() {
		return this.get();
	}

	static ConfigPredicate allOf(List<ConfigPredicate> predicates) {
		return new AllOfPredicate(predicates);
	}

	static ConfigPredicate allOf(ConfigPredicate... predicates) {
		return allOf(List.of(predicates));
	}

	static ConfigPredicate allOf(ConfigPredicate a, ConfigPredicate b) {
		return allOf(List.of(a, b));
	}

	static ConfigPredicate anyOf(List<ConfigPredicate> predicates) {
		return new AnyOfPredicate(predicates);
	}

	static ConfigPredicate anyOf(ConfigPredicate... predicates) {
		return anyOf(List.of(predicates));
	}

	static ConfigPredicate anyOf(ConfigPredicate a, ConfigPredicate b) {
		return anyOf(List.of(a, b));
	}

	static ConfigPredicate allMatch(List<ConfigPredicate> predicates) {
		return new AllMatchPredicate(predicates);
	}

	static ConfigPredicate allMatch(ConfigPredicate... predicates) {
		return allMatch(List.of(predicates));
	}

	static ConfigPredicate allMatch(ConfigPredicate a, ConfigPredicate b) {
		return allMatch(List.of(a, b));
	}

	static <T> ConfigPredicate equalTo(ID entryId, T value) {
		return new EqualToPredicate<>(entryId, value);
	}

	static <T> ConfigPredicate equalTo(ConfigEntry<T> entry, T value) {
		return equalTo(entry.id(), value);
	}

	static <T> ConfigPredicate notEqualTo(ID entryId, T value) {
		return not(equalTo(entryId, value));
	}

	static <T> ConfigPredicate notEqualTo(ConfigEntry<T> entry, T value) {
		return notEqualTo(entry.id(), value);
	}

	static <T> ConfigPredicate greaterThan(ID entryId, T value) {
		return new GreaterThanPredicate<>(entryId, value);
	}

	static <T> ConfigPredicate greaterThan(ConfigEntry<T> entry, T value) {
		return greaterThan(entry.id(), value);
	}

	static <T> ConfigPredicate greaterThanOrEqualTo(ID entryId, T value) {
		return new GreaterThanOrEqualToPredicate<>(entryId, value);
	}

	static <T> ConfigPredicate greaterThanOrEqualTo(ConfigEntry<T> entry, T value) {
		return greaterThanOrEqualTo(entry.id(), value);
	}

	static <T> ConfigPredicate lessThan(ID entryId, T value) {
		return new LessThanPredicate<>(entryId, value);
	}

	static <T> ConfigPredicate lessThan(ConfigEntry<T> entry, T value) {
		return lessThan(entry.id(), value);
	}

	static <T> ConfigPredicate lessThanOrEqualTo(ID entryId, T value) {
		return new LessThanOrEqualToPredicate<>(entryId, value);
	}

	static <T> ConfigPredicate lessThanOrEqualTo(ConfigEntry<T> entry, T value) {
		return lessThanOrEqualTo(entry.id(), value);
	}

	static ConfigPredicate exists(ID entryId) {
		return new ExistsPredicate(entryId);
	}

	static ConfigPredicate exists(ConfigEntry<?> entry) {
		return exists(entry.id());
	}

	static ConfigPredicate selector(ConfigPredicate selector, ConfigPredicate whenTrue, ConfigPredicate whenFalse) {
		return new SelectorPredicate(selector, whenTrue, whenFalse);
	}

	static ConfigPredicate withFallback(ID id, ConfigPredicate predicate, ConfigPredicate fallback) {
		return WithFallbackPredicate.of(id, predicate, fallback);
	}

	static ConfigPredicate withFallback(ConfigEntry<?> entry, ConfigPredicate predicate, ConfigPredicate fallback) {
		return withFallback(entry.id(), predicate, fallback);
	}

	static ConfigPredicate not(ConfigPredicate predicate) {
		return new NotPredicate(predicate);
	}

	static ConfigPredicate alwaysTrue() {
		return TruePredicate.INSTANCE;
	}

	static ConfigPredicate isFabric() {
		return FabricPredicate.INSTANCE;
	}

	static ConfigPredicate isNeoForge() {
		return NeoForgePredicate.INSTANCE;
	}

	static ConfigPredicate modLoaded(String modId) {
		return new ModPredicate(modId);
	}

	static ConfigPredicate modNotLoaded(String modId) {
		return not(new ModPredicate(modId));
	}

	default BlockPredicate asBlockPredicate() {
		return new ConfigBlockPredicate(this);
	}

	default PlacementFilter asPlacementFilter() {
		return new ConfigPlacementFilter(this);
	}

	default TreeDecorator asTreeDecorator(TreeDecorator decorator) {
		return new ConfigPredicateDecorator(decorator, this);
	}

	default LootItemCondition asLootCondition() {
		return new ConfigLootCondition(this);
	}

	default SurfaceRules.ConditionSource asConditionSource() {
		return new ConfigConditionSource(this);
	}

	default SpawnCondition asSpawnCondition() {
		return new ConfigCheck(this);
	}
}
