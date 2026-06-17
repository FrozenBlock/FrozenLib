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

package net.frozenblock.lib.sound.api.type;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.sound.impl.MovingSoundManager;

public final class MovingSoundTypes {
	public static final MovingLoopingSoundType LOOPING = new MovingLoopingSoundType(FrozenLibConstants.id("looping"));
	public static final FadingDistanceLoopingMovingSoundType LOOPING_FADING_DISTANCE = new FadingDistanceLoopingMovingSoundType(FrozenLibConstants.id("looping_fading_distance"));

	public static void init() {
		EntityTrackingEvents.START_TRACKING.register(MovingSoundManager::syncWithPlayer);

		MovingSoundType.register(FrozenLibConstants.id("looping"), LOOPING);
		MovingSoundType.register(FrozenLibConstants.id("looping_fading_distance"), LOOPING_FADING_DISTANCE);
	}
}
