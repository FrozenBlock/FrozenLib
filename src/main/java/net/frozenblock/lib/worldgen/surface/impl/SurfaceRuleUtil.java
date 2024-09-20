/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.surface.impl;

import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRules;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;

public class SurfaceRuleUtil {

    public static void injectSurfaceRules(@NotNull NoiseGeneratorSettings settings, ResourceKey<DimensionType> dimension) {
		NoiseGeneratorInterface inter = NoiseGeneratorInterface.class.cast(settings);
		SurfaceRules.RuleSource newRules = FrozenSurfaceRules.getSurfaceRules(dimension);
        if (newRules != null) {
            inter.frozenLib$writeSurfaceRules(newRules);
        }
    }
}
