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

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.biome.Climate;

/**
 * Contains a list of all continentalness parameters used in vanilla worldgen, named for ease of use.
 */
@UtilityClass
public final class Continentalness {
	public static final Climate.Parameter MUSHROOM_FIELDS = Climate.Parameter.span(-1.2F, -1.05F);
	public static final Climate.Parameter DEEP_OCEAN = Climate.Parameter.span(-1.05F, -0.455F);
	public static final Climate.Parameter OCEAN = Climate.Parameter.span(-0.455F, -0.19F);
	public static final Climate.Parameter COAST = Climate.Parameter.span(-0.19F, -0.11F);
	public static final Climate.Parameter INLAND = Climate.Parameter.span(-0.11F, 0.55F);
	public static final Climate.Parameter NEAR_INLAND = Climate.Parameter.span(-0.11F, 0.03F);
	public static final Climate.Parameter MID_INLAND = Climate.Parameter.span(0.03F, 0.3F);
	public static final Climate.Parameter FAR_INLAND = Climate.Parameter.span(0.3F, 1F);
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;

	public static final Climate.Parameter[] CONTINENTALNESSES = new Climate.Parameter[]{
		MUSHROOM_FIELDS,
		DEEP_OCEAN,
		OCEAN,
		COAST,
		INLAND,
		NEAR_INLAND,
		MID_INLAND,
		FAR_INLAND
	};
}
