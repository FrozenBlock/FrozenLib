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

public final class Continentalness {
    public static final Climate.Parameter MUSHROOM_FIELDS = OverworldBiomeBuilderParameters.MUSHROOM_FIELDS_CONTINENTALNESS;
    public static final Climate.Parameter DEEP_OCEAN = OverworldBiomeBuilderParameters.DEEP_OCEAN_CONTINENTALNESS;
    public static final Climate.Parameter OCEAN = OverworldBiomeBuilderParameters.OCEAN_CONTINENTALNESS;
    public static final Climate.Parameter COAST = OverworldBiomeBuilderParameters.COAST_CONTINENTALNESS;
    public static final Climate.Parameter INLAND = OverworldBiomeBuilderParameters.INLAND_CONTINENTALNESS;
    public static final Climate.Parameter NEAR_INLAND = OverworldBiomeBuilderParameters.NEAR_INLAND_CONTINENTALNESS;
    public static final Climate.Parameter MID_INLAND = OverworldBiomeBuilderParameters.MID_INLAND_CONTINENTALNESS;
    public static final Climate.Parameter FAR_INLAND = OverworldBiomeBuilderParameters.FAR_INLAND_CONTINENTALNESS;
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;
}
