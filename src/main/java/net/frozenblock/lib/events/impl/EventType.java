/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.events.impl;

import java.util.List;
import net.frozenblock.lib.entrypoints.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.entrypoints.ServerEventEntrypoint;
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
