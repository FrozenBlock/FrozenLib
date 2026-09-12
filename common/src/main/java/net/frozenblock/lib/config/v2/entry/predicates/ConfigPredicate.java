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
import java.util.function.Supplier;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.entity.api.variant.ConfigCheck;
import net.frozenblock.lib.item.api.loot.predicates.ConfigLootCondition;
import net.frozenblock.lib.levelgen.feature.api.blockpredicates.ConfigBlockPredicate;
import net.frozenblock.lib.levelgen.placement.api.ConfigPlacementFilter;
import net.frozenblock.lib.levelgen.surface.impl.ConfigConditionSource;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface ConfigPredicate extends Supplier<Boolean> {
	Codec<ConfigPredicate> DIRECT_CODEC = FrozenLibRegistries.CONFIG_PREDICATE_TYPE.byNameCodec().dispatch(ConfigPredicate::type, ConfigPredicateType::codec);
	Codec<Holder<ConfigPredicate>> HOLDER_CODEC = RegistryFileCodec.create(FrozenLibRegistries.CONFIG_PREDICATE_PROVIDER, DIRECT_CODEC);
	Codec<HolderSet<ConfigPredicate>> HOLDER_SET_CODEC = RegistryCodecs.homogeneousList(FrozenLibRegistries.CONFIG_PREDICATE_PROVIDER, DIRECT_CODEC);

	ConfigPredicateType<?> type();

	default boolean test() {
		return this.get();
	}

	static ConfigPredicate allOf(HolderSet<ConfigPredicate> predicates) {
		return new AllOfPredicate(predicates);
	}

	static ConfigPredicate allOf(List<ConfigPredicate> predicates) {
		return allOf(HolderSet.direct(predicates.stream().map(Holder::direct).toList()));
	}

	static ConfigPredicate allOf(ConfigPredicate... predicates) {
		return allOf(List.of(predicates));
	}

	static ConfigPredicate anyOf(HolderSet<ConfigPredicate> predicates) {
		return new AnyOfPredicate(predicates);
	}

	static ConfigPredicate anyOf(List<ConfigPredicate> predicates) {
		return anyOf(HolderSet.direct(predicates.stream().map(Holder::direct).toList()));
	}

	static ConfigPredicate anyOf(ConfigPredicate... predicates) {
		return anyOf(List.of(predicates));
	}

	static ConfigPredicate allMatch(HolderSet<ConfigPredicate> predicates) {
		return new AllMatchPredicate(predicates);
	}

	static ConfigPredicate allMatch(List<ConfigPredicate> predicates) {
		return allMatch(HolderSet.direct(predicates.stream().map(Holder::direct).toList()));
	}

	static ConfigPredicate allMatch(ConfigPredicate... predicates) {
		return allMatch(List.of(predicates));
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

	static ConfigPredicate selector(Holder<ConfigPredicate> selector, Holder<ConfigPredicate> whenTrue, Holder<ConfigPredicate> whenFalse) {
		return new SelectorPredicate(selector, whenTrue, whenFalse);
	}

	static ConfigPredicate selector(ConfigPredicate selector, ConfigPredicate whenTrue, ConfigPredicate whenFalse) {
		return selector(selector.asHolder(), whenTrue.asHolder(), whenFalse.asHolder());
	}

	static ConfigPredicate withFallback(ID id, Holder<ConfigPredicate> predicate, Holder<ConfigPredicate> fallback) {
		return WithFallbackPredicate.of(id, predicate, fallback);
	}

	static ConfigPredicate withFallback(ID id, ConfigPredicate predicate, ConfigPredicate fallback) {
		return withFallback(id, predicate.asHolder(), fallback.asHolder());
	}

	static ConfigPredicate withFallback(ConfigEntry<?> entry, Holder<ConfigPredicate> predicate, Holder<ConfigPredicate> fallback) {
		return withFallback(entry.id(), predicate, fallback);
	}

	static ConfigPredicate withFallback(ConfigEntry<?> entry, ConfigPredicate predicate, ConfigPredicate fallback) {
		return withFallback(entry.id(), predicate, fallback);
	}

	static ConfigPredicate not(Holder<ConfigPredicate> predicate) {
		return new NotPredicate(predicate);
	}

	static ConfigPredicate not(ConfigPredicate predicate) {
		return not(predicate.asHolder());
	}

	static ConfigPredicate alwaysTrue() {
		return TrueConfigPredicate.INSTANCE;
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

	default Holder<ConfigPredicate> asHolder() {
		return Holder.direct(this);
	}

	static BlockPredicate blockPredicate(Holder<ConfigPredicate> predicate) {
		return new ConfigBlockPredicate(predicate);
	}

	default BlockPredicate asBlockPredicate() {
		return blockPredicate(this.asHolder());
	}

	static PlacementFilter placementFilter(Holder<ConfigPredicate> predicate) {
		return new ConfigPlacementFilter<>(predicate);
	}

	default PlacementFilter asPlacementFilter() {
		return placementFilter(this.asHolder());
	}

	static LootItemCondition lootCondition(Holder<ConfigPredicate> predicate) {
		return new ConfigLootCondition(predicate);
	}

	default LootItemCondition asLootCondition() {
		return lootCondition(this.asHolder());
	}

	static SurfaceRules.ConditionSource conditionSource(Holder<ConfigPredicate> predicate) {
		return new ConfigConditionSource(predicate);
	}

	default SurfaceRules.ConditionSource asConditionSource() {
		return conditionSource(this.asHolder());
	}

	static SpawnCondition spawnCondition(Holder<ConfigPredicate> predicate) {
		return new ConfigCheck(predicate);
	}

	default SpawnCondition asSpawnCondition() {
		return spawnCondition(this.asHolder());
	}

	static boolean lookupAndTest(RegistryAccess registries, ResourceKey<ConfigPredicate> key) {
		return registries.lookup(FrozenLibRegistries.CONFIG_PREDICATE_PROVIDER)
			.map(registry -> registry.get(key)
				.map(Holder.Reference::value)
				.map(ConfigPredicate::test)
				.orElse(false)
			).orElse(false);
	}
}
