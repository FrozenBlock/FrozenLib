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
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class ServerLevelEvents {

	/**
	 * Called just after a level is loaded by a Minecraft server.
	 */
	public static final Event<Load> LOAD = FrozenEvents.createEnvironmentEvent(Load.class, callbacks -> (server, level) -> {
		for (Load callback : callbacks) {
			callback.onLevelLoad(server, level);
		}
	});

	/**
	 * Called before a level is unloaded by a Minecraft server.
	 */
	public static final Event<Unload> UNLOAD = FrozenEvents.createEnvironmentEvent(Unload.class, callbacks -> (server, level) -> {
		for (Unload callback : callbacks) {
			callback.onLevelUnload(server, level);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onLevelLoad(MinecraftServer server, ServerLevel level);
	}

	@FunctionalInterface
	public interface Unload {
		void onLevelUnload(MinecraftServer server, ServerLevel level);
	}
}
