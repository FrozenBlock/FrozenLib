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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.frozenblock.lib.event.api.events.FrozenLibServerTickEvents;

@UtilityClass
public class FabricServerTickEventsBridge {
	public static void init() {
		ServerTickEvents.START_SERVER_TICK.register(server ->
			FrozenLibServerTickEvents.START_SERVER_TICK.invoker().onStartTick(server)
		);

		ServerTickEvents.END_SERVER_TICK.register(server ->
			FrozenLibServerTickEvents.END_SERVER_TICK.invoker().onEndTick(server)
		);

		ServerTickEvents.START_LEVEL_TICK.register(level ->
			FrozenLibServerTickEvents.START_LEVEL_TICK.invoker().onStartTick(level)
		);

		ServerTickEvents.END_LEVEL_TICK.register(level ->
			FrozenLibServerTickEvents.END_LEVEL_TICK.invoker().onEndTick(level)
		);
	}
}
