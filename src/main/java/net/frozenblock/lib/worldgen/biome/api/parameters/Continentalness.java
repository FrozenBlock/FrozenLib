/*
 * Copyright 2023 FrozenBlock
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

public final class Continentalness {
    public static final Climate.Parameter MUSHROOM_FIELDS = OverworldBiomeBuilderParameters.mushroomFieldsContinentalness;
    public static final Climate.Parameter DEEP_OCEAN = OverworldBiomeBuilderParameters.deepOceanContinentalness;
    public static final Climate.Parameter OCEAN = OverworldBiomeBuilderParameters.oceanContinentalness;
    public static final Climate.Parameter COAST = OverworldBiomeBuilderParameters.coastContinentalness;
    public static final Climate.Parameter INLAND = OverworldBiomeBuilderParameters.inlandContinentalness;
    public static final Climate.Parameter NEAR_INLAND = OverworldBiomeBuilderParameters.nearInlandContinentalness;
    public static final Climate.Parameter MID_INLAND = OverworldBiomeBuilderParameters.midInlandContinentalness;
    public static final Climate.Parameter FAR_INLAND = OverworldBiomeBuilderParameters.farInlandContinentalness;
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;
}
