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

package net.frozenblock.lib.config.api.registry;

import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.frozenblock.lib.platform.api.ClientOnly;

@FunctionalInterface
public interface ConfigLoadEvent extends CommonEventEntrypoint {
	Event<ConfigLoadEvent> EVENT = EventRegistry.createEnvironmentEvent(ConfigLoadEvent.class, callbacks -> config -> {
		for (var callback : callbacks) callback.onLoad(config);
	});

	void onLoad(Config<?> config) throws Exception;

	@ClientOnly
	interface Client extends ClientEventEntrypoint {
		Event<Client> EVENT = EventRegistry.createEnvironmentEvent(Client.class, callbacks -> config -> {
			for (var callback : callbacks) callback.onLoad(config);
		});

		void onLoad(Config<?> config) throws Exception;
	}
}
