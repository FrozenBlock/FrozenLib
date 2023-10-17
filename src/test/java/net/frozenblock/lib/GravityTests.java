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

package net.frozenblock.lib;

import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GravityTests {

    @Test
    void testGravity() {
        // gravity 0.1 y 300..319
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(300, 319, true, true, new GravityAPI.AbsoluteGravityFunction(0.1)));
        // gravity 100.0 y -64..-32
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(-64, -32, true, true, new GravityAPI.AbsoluteGravityFunction(100.0)));
        // gravity 0.5 y 0..15
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(0, 15, true, true, new GravityAPI.AbsoluteGravityFunction(0.5)));

        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, 300), 0.1);
        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, -64), 100.0);
        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, 0), 0.5);
    }
}