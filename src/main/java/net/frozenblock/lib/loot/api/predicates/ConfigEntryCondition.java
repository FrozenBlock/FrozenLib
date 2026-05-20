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

package net.frozenblock.lib.loot.api.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ConfigEntryCondition implements LootItemCondition {
	public static final MapCodec<ConfigEntryCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryCondition::new));
	private final ConfigEntryPredicate<?> configEntryPredicate;

	public ConfigEntryCondition(ConfigEntryPredicate<?> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	public static <T> ConfigEntryCondition of(ID entryId, ConfigEntryPredicate.Operator operator, T target) {
		return new ConfigEntryCondition(new ConfigEntryPredicate<>(entryId, operator, target));
	}

	public static <T> ConfigEntryCondition equalTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.EQUAL_TO, target);
	}

	public static <T> ConfigEntryCondition notEqualTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.NOT_EQUAL_TO, target);
	}

	public static <T> ConfigEntryCondition greaterThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.GREATER_THAN, target);
	}

	public static <T> ConfigEntryCondition lessThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.LESS_THAN, target);
	}

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return this.configEntryPredicate.evaluate();
	}
}
