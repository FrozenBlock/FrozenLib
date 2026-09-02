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
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

/**
 * Appends a {@link MaterialRule} to a set of {@link DimensionType}s.
 */
public record MaterialRuleAddition(HolderSet<DimensionType> dimensions, boolean hasPreliminarySurface, MaterialRule materialRule) {
	public static final Codec<MaterialRuleAddition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.DIMENSION_TYPE).fieldOf("dimensions").forGetter(MaterialRuleAddition::dimensions),
		Codec.BOOL.optionalFieldOf("has_preliminary_surface", false).forGetter(MaterialRuleAddition::hasPreliminarySurface),
		MaterialRule.CODEC.fieldOf("rule").forGetter(MaterialRuleAddition::materialRule)
	).apply(instance, MaterialRuleAddition::new));

	public MaterialRuleAddition(HolderSet<DimensionType> dimensions, MaterialRule materialRule) {
		this(dimensions, false, materialRule);
	}

	public boolean matches(Holder<DimensionType> dimension) {
		return this.dimensions.contains(dimension);
	}

	public boolean noPreliminarySurface() {
		return !this.hasPreliminarySurface;
	}
}
