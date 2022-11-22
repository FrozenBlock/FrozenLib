/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import net.minecraft.world.level.biome.Climate;

public final class Erosion {

	public static final Climate.Parameter[] erosions = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.78F),
			Climate.Parameter.span(-0.78F, -0.375F),
			Climate.Parameter.span(-0.375F, -0.2225F),
			Climate.Parameter.span(-0.2225F, 0.05F),
			Climate.Parameter.span(0.05F, 0.45F),
			Climate.Parameter.span(0.45F, 0.55F),
			Climate.Parameter.span(0.55F, 1.0F)
	};

	public static final Climate.Parameter EROSION_0 = erosions[0];
	public static final Climate.Parameter EROSION_1 = erosions[1];
	public static final Climate.Parameter EROSION_2 = erosions[2];
	public static final Climate.Parameter EROSION_3 = erosions[3];
	public static final Climate.Parameter EROSION_4 = erosions[4];
	public static final Climate.Parameter EROSION_5 = erosions[5];
	public static final Climate.Parameter EROSION_6 = erosions[6];
	public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
}
