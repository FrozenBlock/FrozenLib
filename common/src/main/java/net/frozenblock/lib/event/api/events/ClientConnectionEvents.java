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

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;

@ClientOnly
public class ClientConnectionEvents {
	public static final Event<Join> JOIN = EventRegistry.createEnvironmentEvent(Join.class, callbacks -> (handler, client) -> {
		for (Join callback : callbacks) {
			callback.onJoin(handler, client);
		}
	});

	public static final Event<Disconnect> DISCONNECT = EventRegistry.createEnvironmentEvent(Disconnect.class, callbacks -> (handler, client) -> {
		for (Disconnect callback : callbacks) {
			callback.onDisconnect(handler, client);
		}
	});

	@FunctionalInterface
	public interface Join {
		void onJoin(ClientPacketListener handler, Minecraft client);
	}

	@FunctionalInterface
	public interface Disconnect {
		void onDisconnect(@Nullable ClientPacketListener handler, Minecraft client);
	}
}
