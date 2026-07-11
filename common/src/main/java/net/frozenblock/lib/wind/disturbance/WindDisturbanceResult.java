/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.wind.disturbance;

import net.minecraft.world.phys.Vec3;

public sealed interface WindDisturbanceResult {
	Pass PASS = new Pass();

	static Success success(double strength, double weight, Vec3 vector) {
		return new Success(strength, weight, vector);
	}

	record Success(double strength, double weight, Vec3 vector) implements WindDisturbanceResult {}

	record Pass() implements WindDisturbanceResult {
		public Pass() {}
	}
}
