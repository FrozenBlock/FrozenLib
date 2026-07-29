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

package net.frozenblock.lib.event.api.events.client;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

@UtilityClass
public final class ClientEntityLifecycleEvents {
	/**
	 * The event that is triggered when an entity is loaded on the client.
	 */
	public static final Event<Load> ENTITY_LOAD = EventRegistry.createEnvironmentEvent(Load.class, callbacks -> (entity, level) -> {
		for (Load callback : callbacks) {
			callback.onEntityLoad(entity, level);
		}
	});

	/**
	 * The event that is triggered when an entity is unloaded on the client.
	 */
	public static final Event<Unload> ENTITY_UNLOAD = EventRegistry.createEnvironmentEvent(Unload.class, callbacks -> (entity, level) -> {
		for (Unload callback : callbacks) {
			callback.onEntityUnload(entity, level);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onEntityLoad(Entity entity, ClientLevel level);
	}

	@FunctionalInterface
	public interface Unload {
		void onEntityUnload(Entity entity, ClientLevel level);
	}
}
