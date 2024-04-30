/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
		// Create an array-backed event
		var event = EventFactory.createArrayBacked(type, invokerFactory);

		// Register the event
		register(event, type);

		// Return the newly created event
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
		// Create an array-backed event
		var event = EventFactory.createArrayBacked(type, emptyInvoker, invokerFactory);

		// Register the event
		register(event, type);

		// Return the newly created event
		return event;
	}

	/**
	 * Registers the specified event.
	 *
	 * @param event The event to be registered
	 * @param type The type of the event to be registered
	 */
	public static <T> void register(Event<T> event, Class<? super T> type) {
		// Check if the event is already registered
		if (!REGISTERED_EVENTS.contains(event)) {
			// Add the event to the list of registered events
			REGISTERED_EVENTS.add(event);

			// Loop through all event types
			for (var eventType : EventType.VALUES) {
				// Check if the listener type is assignable from the event type
				if (eventType.listener().isAssignableFrom(type)) {
					// Get the entrypoints for the specified listener type
					List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

					// Loop through the entrypoints
					for (Object entrypoint : entrypoints) {
						// Check if the entrypoint is assignable from the event type
						if (type.isAssignableFrom(entrypoint.getClass())) {
							// Register the entrypoint to the event
							event.register(Event.DEFAULT_PHASE, (T) entrypoint);
						}
					}
					// Break the loop once a match is found
					break;
				}
			}
		}
	}
}
