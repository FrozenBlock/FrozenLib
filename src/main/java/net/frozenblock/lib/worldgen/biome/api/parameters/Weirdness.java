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
 * Contains a list of all weirdness parameters used in vanilla worldgen, named for ease of use.
 */
@UtilityClass
public final class Weirdness {
	public static final Climate.Parameter MID_SLICE_NORMAL_ASCENDING = Climate.Parameter.span(-1F, -0.93333334F);
	public static final Climate.Parameter HIGH_SLICE_NORMAL_ASCENDING = Climate.Parameter.span(-0.93333334F, -0.7666667F);
	public static final Climate.Parameter PEAK_NORMAL = Climate.Parameter.span(-0.7666667F, -0.56666666F);
	public static final Climate.Parameter HIGH_SLICE_NORMAL_DESCENDING = Climate.Parameter.span(-0.56666666F, -0.4F);
	public static final Climate.Parameter MID_SLICE_NORMAL_DESCENDING = Climate.Parameter.span(-0.4F, -0.26666668F);
	public static final Climate.Parameter LOW_SLICE_NORMAL_DESCENDING = Climate.Parameter.span(-0.26666668F, -0.05F);
	public static final Climate.Parameter VALLEY = Climate.Parameter.span(-0.05F, 0.05F);
	public static final Climate.Parameter LOW_SLICE_VARIANT_ASCENDING = Climate.Parameter.span(0.05F, 0.26666668F);
	public static final Climate.Parameter MID_SLICE_VARIANT_ASCENDING = Climate.Parameter.span(0.26666668F, 0.4F);
	public static final Climate.Parameter HIGH_SLICE_VARIANT_ASCENDING = Climate.Parameter.span(0.4F, 0.56666666F);
	public static final Climate.Parameter PEAK_VARIANT = Climate.Parameter.span(0.56666666F, 0.7666667F);
	public static final Climate.Parameter HIGH_SLICE_VARIANT_DESCENDING = Climate.Parameter.span(0.7666667F, 0.93333334F);
	public static final Climate.Parameter MID_SLICE_VARIANT_DESCENDING = Climate.Parameter.span(0.93333334F, 1F);
	public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;

	public static final Climate.Parameter[] WEIRDNESSES = new Climate.Parameter[]{
		MID_SLICE_NORMAL_ASCENDING,
		HIGH_SLICE_NORMAL_ASCENDING,
		PEAK_NORMAL,
		HIGH_SLICE_NORMAL_DESCENDING,
		MID_SLICE_NORMAL_DESCENDING,
		LOW_SLICE_NORMAL_DESCENDING,
		VALLEY,
		LOW_SLICE_VARIANT_ASCENDING,
		MID_SLICE_VARIANT_ASCENDING,
		HIGH_SLICE_VARIANT_ASCENDING,
		PEAK_VARIANT,
		HIGH_SLICE_VARIANT_DESCENDING,
		MID_SLICE_VARIANT_DESCENDING,
	};
}
