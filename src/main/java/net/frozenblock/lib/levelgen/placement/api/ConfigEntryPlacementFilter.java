/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.placement.api;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.frozenblock.lib.levelgen.placement.impl.FrozenLibPlacementModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConfigEntryPlacementFilter<T> extends PlacementFilter {
	public static final MapCodec<ConfigEntryPlacementFilter<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate)
	).apply(instance, ConfigEntryPredicate::asPlacementFilter));
	private final ConfigEntryPredicate<T> configEntryPredicate;

	public ConfigEntryPlacementFilter(ConfigEntryPredicate<T> configEntryPredicate) {
		this.configEntryPredicate = configEntryPredicate;
	}

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		return this.configEntryPredicate.test();
	}

	@Override
	public PlacementModifierType<?> type() {
		return FrozenLibPlacementModifiers.CONFIG_ENTRY;
	}
}
