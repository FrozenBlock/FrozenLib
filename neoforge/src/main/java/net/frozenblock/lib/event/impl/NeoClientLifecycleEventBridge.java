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
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.event.api.events.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@UtilityClass
public final class NeoClientLifecycleEventBridge {

	private static boolean clientStartedFired = false;

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> {
			if (!clientStartedFired) {
				clientStartedFired = true;
				ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(Minecraft.getInstance());
			}
		});

		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event ->
			ClientConnectionEvents.JOIN.invoker().onJoin(Minecraft.getInstance().getConnection(), Minecraft.getInstance())
		);
		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
			ClientConnectionEvents.DISCONNECT.invoker().onDisconnect(null, Minecraft.getInstance())
		);
	}
}
