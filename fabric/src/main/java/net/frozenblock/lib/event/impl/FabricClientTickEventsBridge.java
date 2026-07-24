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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@UtilityClass
public class FabricClientTickEventsBridge {
	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(client ->
			net.frozenblock.lib.event.api.events.client.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(client)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client ->
			net.frozenblock.lib.event.api.events.client.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(client)
		);

		ClientTickEvents.START_LEVEL_TICK.register(level ->
			net.frozenblock.lib.event.api.events.client.ClientTickEvents.START_LEVEL_TICK.invoker().onStartTick(level)
		);

		ClientTickEvents.END_LEVEL_TICK.register(level ->
			net.frozenblock.lib.event.api.events.client.ClientTickEvents.END_LEVEL_TICK.invoker().onEndTick(level)
		);
	}
}
