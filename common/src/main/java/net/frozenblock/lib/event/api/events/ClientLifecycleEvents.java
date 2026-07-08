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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class ClientLifecycleEvents {

	public static final Event<ClientStarted> CLIENT_STARTED = FrozenEvents.createEnvironmentEvent(ClientStarted.class, callbacks -> client -> {
		for (ClientStarted callback : callbacks) {
			callback.onClientStarted(client);
		}
	});

	public static final Event<ClientStopping> CLIENT_STOPPING = FrozenEvents.createEnvironmentEvent(ClientStopping.class, callbacks -> client -> {
		for (ClientStopping callback : callbacks) {
			callback.onClientStopping(client);
		}
	});

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface ClientStarted {
		void onClientStarted(Minecraft client);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface ClientStopping {
		void onClientStopping(Minecraft client);
	}
}
