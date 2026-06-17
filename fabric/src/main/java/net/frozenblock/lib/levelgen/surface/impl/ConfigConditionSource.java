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

package net.frozenblock.lib.levelgen.surface.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record ConfigConditionSource(ConfigPredicate configPredicate) implements SurfaceRules.ConditionSource {
	public static final MapCodec<ConfigConditionSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("config_predicate").forGetter(ConfigConditionSource::configPredicate)
	).apply(instance, ConfigConditionSource::new));

	@Override
	public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
		return CODEC;
	}

	@Override
	public SurfaceRules.Condition apply(SurfaceRules.Context context) {
		class ConfigPredicateCondition extends SurfaceRules.LazyYCondition {
			ConfigPredicateCondition(SurfaceRules.Context context) {
				super(context);
			}

			protected boolean compute() {
				return ConfigConditionSource.this.configPredicate().test();
			}
		}

		return new ConfigPredicateCondition(context);
	}
}
