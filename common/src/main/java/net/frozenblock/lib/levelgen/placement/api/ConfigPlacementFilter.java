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
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;

public class ConfigPlacementFilter<T> implements PlacementFilter {
	public static final MapCodec<ConfigPlacementFilter<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("config_predicate").forGetter(config -> config.configPredicate)
	).apply(instance, ConfigPlacementFilter::new));
	private final ConfigPredicate configPredicate;

	public ConfigPlacementFilter(ConfigPredicate configPredicate) {
		this.configPredicate = configPredicate;
	}

	@Override
	public boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		return this.configPredicate.test();
	}

	@Override
	public MapCodec<ConfigPlacementFilter<?>> codec() {
		return CODEC;
	}
}
