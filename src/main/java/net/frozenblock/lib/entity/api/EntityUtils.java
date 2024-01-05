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

package net.frozenblock.lib.entity.api;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityUtils {

	public static Optional<Direction> getMovementDirectionHorizontal(@NotNull Entity entity) {
		Direction direction = null;
		Vec3 deltaMovement = entity.getDeltaMovement();
		if (deltaMovement.horizontalDistance() > 0) {
			double nonNegX = Math.abs(deltaMovement.x);
			double nonNegZ = Math.abs(deltaMovement.z);
			if (nonNegX > nonNegZ) {
				direction = deltaMovement.x > 0 ? Direction.EAST : Direction.WEST;
			} else if (nonNegZ > 0) {
				direction = deltaMovement.z > 0 ? Direction.SOUTH : Direction.NORTH;
			}
		}
		return direction != null ? Optional.of(direction) : Optional.empty();
	}

}
