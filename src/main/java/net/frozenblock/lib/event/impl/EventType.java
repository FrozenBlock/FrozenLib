/*
 * Copyright (C) 2024 FrozenBlock
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

import java.util.List;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.ServerEventEntrypoint;
import org.jetbrains.annotations.ApiStatus;

/**
 * Enum class representing the different environments for events to take place on
 */
@ApiStatus.Internal
public enum EventType {

	/**
	 * Represents a client-sided event.
	 */
	CLIENT("frozenlib:client_events", ClientEventEntrypoint.class),

	/**
	 * Represents a common event.
	 */
	COMMON("frozenlib:events", CommonEventEntrypoint.class),

	/**
	 * Represents a server-sided event.
	 */
	SERVER("frozenlib:server_events", ServerEventEntrypoint.class);

	/**
	 * A list of all the possible event types.
	 */
	public static final List<EventType> VALUES = List.of(values());

	/**
	 * The entrypoint string for the event type.
	 */
	private final String entrypoint;

	/**
	 * The listener class for the event type.
	 */
	private final Class<?> listener;

	/**
	 * Constructor for the EventType enum.
	 *
	 * @param entrypoint The entrypoint string for the event type.
	 * @param listener The listener class for the event type.
	 */
	EventType(String entrypoint, Class<?> listener) {
		this.entrypoint = entrypoint;
		this.listener = listener;
	}

	/**
	 * Returns the entrypoint string for the event type.
	 *
	 * @return The entrypoint string.
	 */
	public String entrypoint() {
		return this.entrypoint;
	}

	/**
	 * Returns the listener class for the event type.
	 *
	 * @return The listener class.
	 */
	public Class<?> listener() {
		return this.listener;
	}
}
