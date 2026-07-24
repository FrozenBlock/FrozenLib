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
import net.frozenblock.lib.event.api.events.client.ClientConnectionEvents;
import net.frozenblock.lib.event.api.events.client.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class NeoClientLifecycleEventBridge {

	@ApiStatus.Internal
	public static void init() {
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientStartedEvent.class, event -> {
			ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(event.getClient());
		});
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ClientStoppingEvent.class, event -> {
			ClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(event.getClient());
		});

		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event ->
			ClientConnectionEvents.JOIN.invoker().onJoin(Minecraft.getInstance().getConnection(), Minecraft.getInstance())
		);
		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
			ClientConnectionEvents.DISCONNECT.invoker().onDisconnect(null, Minecraft.getInstance())
		);
	}
}
