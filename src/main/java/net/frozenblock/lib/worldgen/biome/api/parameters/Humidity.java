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

/**
 * Contains a list of all humidity parameters used in vanilla worldgen, named for ease of use.
 */
public final class Humidity {
    public static final Climate.Parameter ARID = Climate.Parameter.span(-1F, -0.35F);
    public static final Climate.Parameter DRY = Climate.Parameter.span(-0.35F, -0.1F);
    public static final Climate.Parameter NEUTRAL = Climate.Parameter.span(-0.1F, 0.1F);
    public static final Climate.Parameter WET = Climate.Parameter.span(0.1F, 0.3F);
    public static final Climate.Parameter HUMID = Climate.Parameter.span(0.3F, 1F);
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;

	public static final Climate.Parameter[] HUMIDITIES = new Climate.Parameter[]{
		ARID,
		DRY,
		NEUTRAL,
		WET,
		HUMID
	};
}
