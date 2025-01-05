/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.gravity.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface GravityFunction {
	/***
	 * @param entity The optional entity being tracked
	 * @param y The current y position
	 * @param minY The minimum Y position of the gravity belt
	 * @param maxY The maximum Y position of the gravity belt
	 * @return The gravity value
	 */
	Vec3 get(@Nullable Entity entity, double y, double minY, double maxY);
}
