/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
