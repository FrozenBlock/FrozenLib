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

package net.frozenblock.lib.entity.api.behavior;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

/**
 * A smoother version of {@link SmoothSwimmingMoveControl}.
 * <p>
 * Implemented via {@link net.frozenblock.lib.entity.mixin.behavior.SmoothSwimmingMoveControlMixin SmoothSwimmingMoveControlMixin}.
 */
public class SmootherSwimmingMoveControl<T extends Mob> extends SmoothSwimmingMoveControl<T> {

	public SmootherSwimmingMoveControl(T mob, int maxTurnX, int maxTurnY, float inWaterSpeedModifier, float outsideWaterSpeedModifier, boolean applyGravity) {
		super(mob, maxTurnX, maxTurnY, inWaterSpeedModifier, outsideWaterSpeedModifier, applyGravity);
	}
}
