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

package net.frozenblock.lib.wind.disturbance.geyser;

import net.minecraft.world.phys.AABB;

/**
 * Implemented in {@code PotentSulfurBlockEntity} so {@code GeyserWindDisturbance} can tell what the current wind area is and if the eruption is active.
 */
public interface PotentSulfurWindAccess {

	default void frozenLib$pingWindActive(AABB area, long gameTime) {
		throw new AssertionError();
	}

	default AABB frozenLib$getWindArea() {
		throw new AssertionError();
	}

	default boolean frozenLib$isWindActive(long currentGameTime) {
		throw new AssertionError();
	}
}
