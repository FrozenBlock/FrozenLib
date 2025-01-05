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

package net.frozenblock.lib.config.api.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;

@FunctionalInterface
public interface ConfigSaveEvent extends CommonEventEntrypoint {

	Event<ConfigSaveEvent> EVENT = FrozenEvents.createEnvironmentEvent(ConfigSaveEvent.class, callbacks -> config -> {
		for (var callback : callbacks) {
			callback.onSave(config);
		}
	});

	void onSave(Config<?> config) throws Exception;

	@Environment(EnvType.CLIENT)
	interface Client extends ClientEventEntrypoint {

		Event<Client> EVENT = FrozenEvents.createEnvironmentEvent(Client.class, callbacks -> config -> {
			for (var callback : callbacks) {
				callback.onSave(config);
			}
		});

		void onSave(Config<?> config) throws Exception;
	}
}
