/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.event.impl;

import java.util.List;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.entrypoint.api.ServerEventEntrypoint;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum EventType {
	CLIENT("frozenlib:client_events", ClientEventEntrypoint.class),
	COMMON("frozenlib:events", CommonEventEntrypoint.class),
	SERVER("frozenlib:server_events", ServerEventEntrypoint.class);

	public static final List<EventType> VALUES = List.of(values());

	private final String entrypoint;
	private final Class<?> listener;

	EventType(String entrypoint, Class<?> listener) {
		this.entrypoint = entrypoint;
		this.listener = listener;
	}

	public String entrypoint() {
		return this.entrypoint;
	}

	public Class<?> listener() {
		return this.listener;
	}
}
