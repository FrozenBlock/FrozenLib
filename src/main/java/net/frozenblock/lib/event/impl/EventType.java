/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
