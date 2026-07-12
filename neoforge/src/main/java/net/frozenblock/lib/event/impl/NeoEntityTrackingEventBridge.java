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

package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@UtilityClass
public class NeoEntityTrackingEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(PlayerEvent.StartTracking.class, NeoEntityTrackingEventBridge::onStartTracking);
		NeoForge.EVENT_BUS.addListener(PlayerEvent.StopTracking.class, NeoEntityTrackingEventBridge::onStopTracking);
	}

	private static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			EntityTrackingEvents.START_TRACKING.invoker().onStartTracking(event.getTarget(), player);
		}
	}

	private static void onStopTracking(PlayerEvent.StopTracking event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			EntityTrackingEvents.STOP_TRACKING.invoker().onStopTracking(event.getTarget(), player);
		}
	}
}
