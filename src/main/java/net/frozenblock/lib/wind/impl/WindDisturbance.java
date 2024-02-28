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

package net.frozenblock.lib.wind.impl;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindDisturbance {
	public static final DisturbanceResult DUMMY_RESULT = new DisturbanceResult(0D, 0D, Vec3.ZERO);

	private final Vec3 origin;
	private final AABB affectedArea;
	private final DisturbanceLogic disturbanceLogic;

    public WindDisturbance(Vec3 origin, AABB affectedArea, DisturbanceLogic disturbanceLogic) {
        this.origin = origin;
        this.affectedArea = affectedArea;
        this.disturbanceLogic = disturbanceLogic;
    }

	public DisturbanceResult calculateDisturbanceResult(Level level, Vec3 windTarget) {
		if (this.affectedArea.contains(windTarget)) {
			DisturbanceResult disturbanceResult = this.disturbanceLogic.calculateDisturbanceResult(level, this.origin, this.affectedArea, windTarget);
			if (disturbanceResult != null) {
				return disturbanceResult;
			}
		}
		return DUMMY_RESULT;
	}

    @FunctionalInterface
	public interface DisturbanceLogic {
		DisturbanceResult calculateDisturbanceResult(Level level, Vec3 windOrigin, AABB affectedArea, Vec3 windTarget);
	}

	public record DisturbanceResult(double strength, double weight, Vec3 wind) {
	}
}
