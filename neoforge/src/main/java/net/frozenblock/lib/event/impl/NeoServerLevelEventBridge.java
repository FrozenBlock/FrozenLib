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

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ServerLevelEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

@UtilityClass
public class NeoServerLevelEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, NeoServerLevelEventBridge::onLevelLoad);
		NeoForge.EVENT_BUS.addListener(LevelEvent.Unload.class, NeoServerLevelEventBridge::onLevelUnload);
	}

	private static void onLevelLoad(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel level) {
			ServerLevelEvents.LOAD.invoker().onLevelLoad(level.getServer(), level);
		}
	}

	private static void onLevelUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerLevel level) {
			ServerLevelEvents.UNLOAD.invoker().onLevelUnload(level.getServer(), level);
		}
	}
}
