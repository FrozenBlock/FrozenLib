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

package net.frozenblock.lib.levelgen.material.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public record ConfigCondition(ConfigPredicate configPredicate) implements MaterialCondition {
	public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("config_predicate").forGetter(ConfigCondition::configPredicate)
	).apply(instance, ConfigCondition::new));

	@Override
	public ConditionEvaluator compile(MaterialRuleContext context) {
		return new MaterialRuleContext.LazyYCondition(context) {
			@Override
			protected boolean compute() {
				return ConfigCondition.this.configPredicate.test();
			}
		};
	}

	@Override
	public MapCodec<? extends MaterialCondition> codec() {
		return CODEC;
	}
}
