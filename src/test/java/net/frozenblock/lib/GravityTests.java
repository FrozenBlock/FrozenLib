/*
 * Copyright 2023-2024 FrozenBlock
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
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.functions.AbsoluteGravityFunction;
import net.minecraft.world.level.Level;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GravityTests {

	@BeforeAll
	public static void setup() {
		TestUtil.setup();
	}

    @Test
    void testGravity() {
        // gravity 0.1 y 300..319
        GravityAPI.register(Level.OVERWORLD, new GravityBelt<>(300.0, 319.0, new AbsoluteGravityFunction(new Vec3(0.0, 0.1, 0.0))));
        // gravity 100.0 y -64..-32
        GravityAPI.register(Level.OVERWORLD, new GravityBelt<>(-64.0, -32.0, new AbsoluteGravityFunction(new Vec3(0.0, 100.0, 0.0))));
        // gravity 0.5 y 0..15
        GravityAPI.register(Level.OVERWORLD, new GravityBelt<>(0.0, 15.0, new AbsoluteGravityFunction(new Vec3(0.0, 0.5, 0.0))));
		
        assertEquals(new Vec3(0.0, 0.1, 0.0), GravityAPI.calculateGravity(Level.OVERWORLD, 300));
        assertEquals(new Vec3(0.0, 100.0, 0.0), GravityAPI.calculateGravity(Level.OVERWORLD, -64));
        assertEquals(new Vec3(0.0, 0.5, 0.0), GravityAPI.calculateGravity(Level.OVERWORLD, 0));
    }
}
