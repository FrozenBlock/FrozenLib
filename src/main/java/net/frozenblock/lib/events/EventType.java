package net.frozenblock.lib.events;

import java.util.List;
import net.frozenblock.lib.entrypoints.ClientEventEntrypoint;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.entrypoints.ServerEventEntrypoint;

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
