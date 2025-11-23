/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.entrypoint.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.event.api.FrozenEvents;

@FunctionalInterface
public interface FrozenClientEntrypoint extends ClientEventEntrypoint {
	Event<FrozenClientEntrypoint> EVENT = FrozenEvents.createEnvironmentEvent(FrozenClientEntrypoint.class, callbacks -> () -> {
		for (var callback : callbacks) {
			callback.init();
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) callback.initDevOnly();
		}
	});

	void init();

	default void initDevOnly() {}

}
