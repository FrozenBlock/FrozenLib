/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.levelgen.feature.impl.blockpredicates.FrozenLibBlockPredicateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class ConfigEntryBlockPredicate implements BlockPredicate {
	public static final MapCodec<ConfigEntryBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryBlockPredicate::new));
	private final ConfigEntryPredicate<?> configEntryPredicate;

	public ConfigEntryBlockPredicate(ConfigEntryPredicate<?> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	public static <T> ConfigEntryBlockPredicate of(ID entryId, ConfigEntryPredicate.Operator operator, T target) {
		return new ConfigEntryBlockPredicate(new ConfigEntryPredicate<>(entryId, operator, target));
	}

	public static <T> ConfigEntryBlockPredicate equalTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.EQUAL_TO, target);
	}

	public static <T> ConfigEntryBlockPredicate notEqualTo(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.NOT_EQUAL_TO, target);
	}

	public static <T> ConfigEntryBlockPredicate greaterThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.GREATER_THAN, target);
	}

	public static <T> ConfigEntryBlockPredicate lessThan(ConfigEntry<T> entry, T target) {
		return of(entry.id(), ConfigEntryPredicate.Operator.LESS_THAN, target);
	}

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.CONFIG_ENTRY;
	}

	@Override
	public boolean test(WorldGenLevel level, BlockPos pos) {
		return this.configEntryPredicate.evaluate();
	}

}
