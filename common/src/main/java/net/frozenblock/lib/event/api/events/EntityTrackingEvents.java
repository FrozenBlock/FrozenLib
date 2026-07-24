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

package net.frozenblock.lib.event.api.events;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class EntityTrackingEvents {
	/**
	 * The event that is triggered when an entity starts being tracked.
	 */
	public static final Event<StartTracking> START_TRACKING = EventRegistry.createEnvironmentEvent(StartTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StartTracking callback : callbacks) {
			callback.onStartTracking(trackedEntity, player);
		}
	});

	/**
	 * The event that is triggered when an entity stops being tracked.
	 */
	public static final Event<StopTracking> STOP_TRACKING = EventRegistry.createEnvironmentEvent(StopTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StopTracking callback : callbacks) {
			callback.onStopTracking(trackedEntity, player);
		}
	});

	@FunctionalInterface
	public interface StartTracking {
		void onStartTracking(Entity trackedEntity, ServerPlayer player);
	}

	@FunctionalInterface
	public interface StopTracking {
		void onStopTracking(Entity trackedEntity, ServerPlayer player);
	}
}
