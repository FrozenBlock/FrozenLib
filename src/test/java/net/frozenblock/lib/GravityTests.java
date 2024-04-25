/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib;

import net.frozenblock.lib.gravity.api.GravityAPI;
import net.frozenblock.lib.gravity.api.GravityBelt;
import net.frozenblock.lib.gravity.api.functions.AbsoluteGravityFunction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
