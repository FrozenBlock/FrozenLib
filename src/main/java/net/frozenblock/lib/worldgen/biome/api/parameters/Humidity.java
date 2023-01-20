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

public final class Humidity {
    public static final Climate.Parameter ARID = OverworldBiomeBuilderParameters.humidities[0];
    public static final Climate.Parameter DRY = OverworldBiomeBuilderParameters.humidities[1];
    public static final Climate.Parameter NEUTRAL = OverworldBiomeBuilderParameters.humidities[2];
    public static final Climate.Parameter WET = OverworldBiomeBuilderParameters.humidities[3];
    public static final Climate.Parameter HUMID = OverworldBiomeBuilderParameters.humidities[4];
    public static final Climate.Parameter FULL_RANGE = OverworldBiomeBuilderParameters.FULL_RANGE;
}
