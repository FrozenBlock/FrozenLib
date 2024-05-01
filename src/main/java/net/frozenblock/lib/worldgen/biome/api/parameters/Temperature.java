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

public final class Temperature {
    public static final Climate.Parameter ICY = OverworldBiomeBuilderParameters.TEMPERATURES[0];
    public static final Climate.Parameter COOL = OverworldBiomeBuilderParameters.TEMPERATURES[1];
    public static final Climate.Parameter NEUTRAL = OverworldBiomeBuilderParameters.TEMPERATURES[2];
    public static final Climate.Parameter WARM = OverworldBiomeBuilderParameters.TEMPERATURES[3];
    public static final Climate.Parameter HOT = OverworldBiomeBuilderParameters.TEMPERATURES[4];

	public static final Climate.Parameter ONE = OverworldBiomeBuilderParameters.TEMPERATURES[0];
	public static final Climate.Parameter TWO = OverworldBiomeBuilderParameters.TEMPERATURES[1];
	public static final Climate.Parameter THREE = OverworldBiomeBuilderParameters.TEMPERATURES[2];
	public static final Climate.Parameter FOUR = OverworldBiomeBuilderParameters.TEMPERATURES[3];
	public static final Climate.Parameter FIVE = OverworldBiomeBuilderParameters.TEMPERATURES[4];

    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;
}
