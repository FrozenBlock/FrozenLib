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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.biome.Climate;

/**
 * Contains a list of all erosion parameters used in vanilla worldgen, for ease of use.
 */
@UtilityClass
public final class Erosion {
	public static final Climate.Parameter EROSION_0 = Climate.Parameter.span(-1F, -0.78F);
	public static final Climate.Parameter EROSION_1 = Climate.Parameter.span(-0.78F, -0.375F);
	public static final Climate.Parameter EROSION_2 = Climate.Parameter.span(-0.375F, -0.2225F);
	public static final Climate.Parameter EROSION_3 = Climate.Parameter.span(-0.2225F, 0.05F);
	public static final Climate.Parameter EROSION_4 = Climate.Parameter.span(0.05F, 0.45F);
	public static final Climate.Parameter EROSION_5 = Climate.Parameter.span(0.45F, 0.55F);
	public static final Climate.Parameter EROSION_6 = Climate.Parameter.span(0.55F, 1F);
	public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;

	public static final Climate.Parameter[] EROSIONS = new Climate.Parameter[]{
		EROSION_0,
		EROSION_1,
		EROSION_2,
		EROSION_3,
		EROSION_4,
		EROSION_5,
		EROSION_6
	};
}
