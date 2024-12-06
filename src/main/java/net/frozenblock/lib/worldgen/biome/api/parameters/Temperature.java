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
 * Contains a list of all tmeperature parameters used in vanilla worldgen, named for ease of use.
 */
public final class Temperature {
    public static final Climate.Parameter ICY = Climate.Parameter.span(-1F, -0.45F);
    public static final Climate.Parameter COOL = Climate.Parameter.span(-0.45F, -0.15F);
    public static final Climate.Parameter NEUTRAL = Climate.Parameter.span(-0.15F, 0.2F);
    public static final Climate.Parameter WARM = Climate.Parameter.span(0.2F, 0.55F);
    public static final Climate.Parameter HOT = Climate.Parameter.span(0.55F, 1F);
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;

	public static final Climate.Parameter FROZEN_RANGE = ICY;
	public static final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(COOL, HOT);

	public static final Climate.Parameter[] TEMPERATURES = new Climate.Parameter[]{
		ICY,
		COOL,
		NEUTRAL,
		WARM,
		HOT
	};
}
