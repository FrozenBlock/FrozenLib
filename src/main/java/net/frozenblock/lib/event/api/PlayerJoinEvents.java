/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.event.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * A class representing the player join event.
 */
public class PlayerJoinEvents {

	/**
	 * The event that is triggered when a player joins the server.
	 */
	public static final Event<PlayerJoin> ON_JOIN_SERVER = FrozenEvents.createEnvironmentEvent(PlayerJoin.class, (callbacks) -> (server, player) -> {
		for (var callback : callbacks) {
			callback.onPlayerJoin(server, player);
		}
	});

	/**
	 * The event that is triggered when a player joins a world.
	 */
	public static final Event<PlayerAddedToLevel> ON_PLAYER_ADDED_TO_LEVEL = FrozenEvents.createEnvironmentEvent(PlayerAddedToLevel.class, (callbacks) -> (server, serverLevel, player) -> {
		for (var callback : callbacks) {
			callback.onPlayerAddedToLevel(server, serverLevel, player);
		}
	});

	/**
	 * A functional interface representing a player join event.
	 */
	@FunctionalInterface
	public interface PlayerJoin extends CommonEventEntrypoint {
		/**
		 * Triggers the event when a player joins the server.
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
		 * Triggers the event when a player is added to a level.
		 * @param server the Minecraft server instance
		 * @param serverLevel the server level the player has been added to
		 * @param player the player added to the level
		 */
		void onPlayerAddedToLevel(MinecraftServer server, ServerLevel serverLevel, ServerPlayer player);
	}
}
