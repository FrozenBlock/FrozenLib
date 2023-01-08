/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.surface.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Holds both a {@link ResourceLocation} and {@link SurfaceRules.RuleSource}.
 * The ResourceLocation denotes the dimension type to be modified, and the RuleSource are the rules to be applied to it.
 */

public class FrozenDimensionBoundRuleSource {
	public final ResourceLocation dimension;
	public final SurfaceRules.RuleSource ruleSource;

	public FrozenDimensionBoundRuleSource(ResourceLocation dimension, SurfaceRules.RuleSource ruleSource) {
		this.dimension = dimension;
		this.ruleSource = ruleSource;
	}
}
