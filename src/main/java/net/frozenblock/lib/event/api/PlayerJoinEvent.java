/*
 * Copyright 2023 FrozenBlock
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

import java.util.ArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerJoinEvent {

	private static final ArrayList<PlayerJoin> JOIN_EVENTS = new ArrayList<>();

	public static void register(PlayerJoin joinEvent) {
		JOIN_EVENTS.add(joinEvent);
	}

	public static void onPlayerJoined(MinecraftServer server, ServerPlayer player) {
		for (PlayerJoin joinEvent : JOIN_EVENTS) {
			joinEvent.onPlayerJoin(server, player);
		}
	}

	@FunctionalInterface
	public interface PlayerJoin {
		void onPlayerJoin(MinecraftServer server, ServerPlayer player);
	}

}
