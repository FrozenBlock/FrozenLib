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

package net.frozenblock.lib.levelgen.material.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Appends a {@link SurfaceRules.RuleSource} to a set of {@link DimensionType}s.
 */
public record RuleSourceAddition(HolderSet<DimensionType> dimensions, boolean hasPreliminarySurface, SurfaceRules.RuleSource ruleSource) {
	public static final Codec<RuleSourceAddition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.homogeneousList(Registries.DIMENSION_TYPE).fieldOf("dimensions").forGetter(RuleSourceAddition::dimensions),
		Codec.BOOL.optionalFieldOf("has_preliminary_surface", false).forGetter(RuleSourceAddition::hasPreliminarySurface),
		SurfaceRules.RuleSource.CODEC.fieldOf("rule_source").forGetter(RuleSourceAddition::ruleSource)
	).apply(instance, RuleSourceAddition::new));

	public RuleSourceAddition(HolderSet<DimensionType> dimensions, SurfaceRules.RuleSource ruleSource) {
		this(dimensions, false, ruleSource);
	}

	public boolean matches(Holder<DimensionType> dimension) {
		return this.dimensions.contains(dimension);
	}

	public boolean noPreliminarySurface() {
		return !this.hasPreliminarySurface;
	}
}
