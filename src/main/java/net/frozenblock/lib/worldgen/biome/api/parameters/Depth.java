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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Depth {

    public static final Climate.Parameter[] depths = new Climate.Parameter[]{
            Climate.Parameter.point(0.0F),
            Climate.Parameter.span(0.2F, 0.9F),
            Climate.Parameter.point(1.0F)
    };

    public static final Climate.Parameter SURFACE = depths[0];
    public static final Climate.Parameter UNDERGROUND = depths[1];
    public static final Climate.Parameter FLOOR = depths[2];
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
}
