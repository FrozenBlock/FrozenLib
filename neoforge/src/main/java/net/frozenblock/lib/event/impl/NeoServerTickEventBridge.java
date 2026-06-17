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

import net.frozenblock.lib.event.api.events.TickEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoServerTickEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ServerTickEvent.Pre.class, NeoServerTickEventBridge::onPreServerTick);
		NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, NeoServerTickEventBridge::onPostServerTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Pre.class, NeoServerTickEventBridge::onPreLevelTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, NeoServerTickEventBridge::onPostLevelTick);
	}

	private static void onPreServerTick(ServerTickEvent.Pre event) {
		TickEvents.START_SERVER_TICK.invoker().onStartTick(event.getServer());
	}
	private static void onPostServerTick(ServerTickEvent.Post event) {
		TickEvents.END_SERVER_TICK.invoker().onEndTick(event.getServer());
	}
	private static void onPreLevelTick(LevelTickEvent.Pre event) {
		if (!event.getLevel().isClientSide()) {
			TickEvents.START_LEVEL_TICK.invoker().onStartTick((ServerLevel) event.getLevel());
		}
	}
	private static void onPostLevelTick(LevelTickEvent.Post event) {
		if (!event.getLevel().isClientSide()) {
			TickEvents.END_LEVEL_TICK.invoker().onEndTick((ServerLevel) event.getLevel());
		}
	}
}
