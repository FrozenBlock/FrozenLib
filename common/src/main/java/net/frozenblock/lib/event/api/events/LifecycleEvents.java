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
import net.minecraft.server.players.PlayerList;

public class LifecycleEvents {
	/**
	 * Called upon NeoForge's {@code ServerAboutToStartEvent} or Fabric's {@code SERVER_STARTING} event.
	 *
	 * <p>This was done to alleviate a crash on NeoForge, as modifying Biomes would crash if using their {@code ServerStarting} event.
	 */
	public static final Event<ServerAboutToStart> SERVER_ABOUT_TO_START_OR_STARTING = FrozenEvents.createEnvironmentEvent(ServerAboutToStart.class, callbacks -> server -> {
		for (ServerAboutToStart callback : callbacks) {
			callback.onServerAboutToStart(server);
		}
	});

	/**
	 * Called when a Minecraft server is starting.
	 *
	 * <p>This occurs before the {@link PlayerList player list} and any levels are loaded.
	 */
	public static final Event<ServerStarting> SERVER_STARTING = FrozenEvents.createEnvironmentEvent(ServerStarting.class, callbacks -> server -> {
		for (ServerStarting callback : callbacks) {
			callback.onServerStarting(server);
		}
	});

	/**
	 * Called when a Minecraft server has started and is about to tick for the first time.
	 *
	 * <p>At this stage, all levels are live.
	 */
	public static final Event<ServerStarted> SERVER_STARTED = FrozenEvents.createEnvironmentEvent(ServerStarted.class, (callbacks) -> (server) -> {
		for (ServerStarted callback : callbacks) {
			callback.onServerStarted(server);
		}
	});

	/**
	 * Called when a Minecraft server has started shutting down.
	 * This occurs before the server's network channel is closed and before any players are disconnected.
	 *
	 * <p>For example, an integrated server will begin stopping, but its client may continue to run.
	 *
	 * <p>All levels are still present and can be modified.
	 */
	public static final Event<ServerStopping> SERVER_STOPPING = FrozenEvents.createEnvironmentEvent(ServerStopping.class, (callbacks) -> (server) -> {
		for (ServerStopping callback : callbacks) {
			callback.onServerStopping(server);
		}
	});

	/**
	 * Called when a Minecraft server has stopped.
	 * All levels have been closed and all (block)entities and players have been unloaded.
	 */
	public static final Event<ServerStopped> SERVER_STOPPED = FrozenEvents.createEnvironmentEvent(ServerStopped.class, callbacks -> server -> {
		for (ServerStopped callback : callbacks) {
			callback.onServerStopped(server);
		}
	});

	@FunctionalInterface
	public interface ServerAboutToStart {
		void onServerAboutToStart(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStarting {
		void onServerStarting(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStarted {
		void onServerStarted(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopping {
		void onServerStopping(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopped {
		void onServerStopped(MinecraftServer server);
	}
}
