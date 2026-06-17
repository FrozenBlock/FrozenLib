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

package net.frozenblock.lib.event.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.event.impl.EventType;

public class FrozenEvents {
	/**
	 * A list to store all the registered events
	 */
	private static final List<Event<?>> REGISTERED_EVENTS = new ArrayList<>();

	/**
	 * Creates an environment event with the specified event type and invoker factory.
	 *
	 * @param type The type of event to be created
	 * @param invokerFactory The function to create the invoker for the event
	 * @return A new Event of the specified type
	 */
	public static <T> Event<T> createEnvironmentEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
		final var event = EventFactory.createArrayBacked(type, invokerFactory);
		register(event, type);
		return event;
	}

	/**
	 * Creates an environment event with the specified event type, empty invoker, and invoker factory.
	 *
	 * @param type The type of event to be created
	 * @param emptyInvoker An empty invoker for the event
	 * @param invokerFactory The function to create the invoker for the event
	 * @return A new Event of the specified type
	 */
	public static <T> Event<T> createEnvironmentEvent(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		final var event = EventFactory.createArrayBacked(type, emptyInvoker, invokerFactory);
		register(event, type);
		return event;
	}

	/**
	 * Registers the specified event.
	 *
	 * @param event The event to be registered
	 * @param type The type of the event to be registered
	 */
	public static <T> void register(Event<T> event, Class<? super T> type) {
		if (REGISTERED_EVENTS.contains(event)) return;

		REGISTERED_EVENTS.add(event);
		// Loop through all event types
		for (var eventType : EventType.VALUES) {
			// Check if the listener type is assignable from the event type
			if (!eventType.listener().isAssignableFrom(type)) continue;
			// Get the entrypoints for the specified listener type
			final List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

			// Loop through the entrypoints
			for (Object entrypoint : entrypoints) {
				// Check if the entrypoint is assignable from the event type
				if (!type.isAssignableFrom(entrypoint.getClass())) continue;
				// Register the entrypoint to the event
				event.register(Event.DEFAULT_PHASE, (T) entrypoint);
			}
			// Break the loop once a match is found
			break;
		}
	}
}
