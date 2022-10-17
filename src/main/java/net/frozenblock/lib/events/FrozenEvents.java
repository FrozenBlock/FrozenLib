package net.frozenblock.lib.events;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class FrozenEvents {

    private static final List<Event<?>> REGISTERED_EVENTS = new ArrayList<>();

    public static <T> Event<T> createEnvironmentEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        var event = EventFactory.createArrayBacked(type, invokerFactory);

        register(event, type);

        return event;
    }

    public static <T> Event<T> createEnvironmentEvent(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
        var event = EventFactory.createArrayBacked(type, emptyInvoker, invokerFactory);

        register(event, type);

        return event;
    }

    public static <T> void register(Event<T> event, Class<? super T> type) {
        if (!REGISTERED_EVENTS.contains(event)) {
            REGISTERED_EVENTS.add(event);
            for (var eventType : EventType.VALUES) {
                if (eventType.listener().isAssignableFrom(type)) {
                    List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

                    for (Object entrypoint : entrypoints) {
                        var map = new Object2ObjectOpenHashMap<Class<?>, ResourceLocation>();

                        if (type.isAssignableFrom(entrypoint.getClass())) {
                            var phase = map.getOrDefault(type, Event.DEFAULT_PHASE);
                            event.register(phase, (T) entrypoint);
                        }
                    }

                    break;
                }
            }
        }
    }
}
