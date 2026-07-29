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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.frozenblock.lib.event.api.events.client.ClientConnectionEvents;

@UtilityClass
public final class FabricClientLifecycleEventBridge {

	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(mc ->
			net.frozenblock.lib.event.api.events.client.ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(mc)
		);
		ClientLifecycleEvents.CLIENT_STOPPING.register(mc ->
			net.frozenblock.lib.event.api.events.client.ClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(mc)
		);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
			ClientConnectionEvents.JOIN.invoker().onJoin(handler, client)
		);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
			ClientConnectionEvents.DISCONNECT.invoker().onDisconnect(handler, client)
		);
	}
}
