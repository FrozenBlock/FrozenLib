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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

@UtilityClass
public final class ServerLevelEvents {
	/**
	 * Called just after a {@link ServerLevel} is loaded by a {@link MinecraftServer}.
	 */
	public static final Event<Load> LOAD = EventRegistry.createEnvironmentEvent(Load.class, callbacks -> (server, level) -> {
		for (Load callback : callbacks) {
			callback.onLevelLoad(server, level);
		}
	});

	/**
	 * Called before a {@link ServerLevel} is unloaded by a {@link MinecraftServer}.
	 */
	public static final Event<Unload> UNLOAD = EventRegistry.createEnvironmentEvent(Unload.class, callbacks -> (server, level) -> {
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
