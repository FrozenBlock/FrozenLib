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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.frozenblock.lib.event.api.events.ClientEntityLifecycleEvents;

@UtilityClass
public class FabricClientEntityLifecycleEventsBridge {
	public static void init() {
		ClientEntityEvents.ENTITY_LOAD.register((entity, level) ->
			ClientEntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(entity, level)
		);
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
			ClientEntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(entity, level)
		);
	}
}
