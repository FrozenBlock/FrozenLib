/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@UtilityClass
public final class PlayerJoinEvents {
	/**
	 * The event that is triggered when a {@link ServerPlayer} joins the {@link MinecraftServer}.
	 */
	public static final Event<PlayerJoin> ON_JOIN_SERVER = EventRegistry.createEnvironmentEvent(PlayerJoin.class, (callbacks) -> (server, player) -> {
		for (var callback : callbacks) callback.onPlayerJoin(server, player);
	});

	/**
	 * The event that is triggered when a {@link ServerPlayer} is added to a {@link ServerLevel}.
	 */
	public static final Event<PlayerAddedToLevel> ON_PLAYER_ADDED_TO_LEVEL = EventRegistry.createEnvironmentEvent(PlayerAddedToLevel.class, (callbacks) -> (server, level, player) -> {
		for (var callback : callbacks) callback.onPlayerAddedToLevel(server, level, player);
	});

	/**
	 * A functional interface representing a player join event.
	 */
	@FunctionalInterface
	public interface PlayerJoin extends CommonEventEntrypoint {
		/**
		 * Triggers the event when a {@link ServerPlayer} joins the {@link MinecraftServer}.
		 * @param server the Minecraft server instance
		 * @param player the player joining the server
		 */
		void onPlayerJoin(MinecraftServer server, ServerPlayer player);
	}

	/**
	 * A functional interface representing a player added to level event.
	 */
	@FunctionalInterface
	public interface PlayerAddedToLevel extends CommonEventEntrypoint {
		/**
		 * Triggers the event when a {@link ServerPlayer} is added to a {@link ServerLevel}.
		 * @param server the Minecraft server instance
		 * @param level the server level the player has been added to
		 * @param player the player added to the level
		 */
		void onPlayerAddedToLevel(MinecraftServer server, ServerLevel level, ServerPlayer player);
	}
}
