package net.frozenblock.lib.events;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FrozenEvents {

    private static final List<Event> REGISTERED_EVENTS = new ArrayList<>();

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
        FrozenMain.LOGGER.info("the events are creating");
        if (!REGISTERED_EVENTS.contains(event)) {
            REGISTERED_EVENTS.add(event);
            for (var eventType : EventType.VALUES) {
                FrozenMain.LOGGER.info("the enum is recognized");
                if (eventType.listener().isAssignableFrom(type)) {
                    List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

                    FrozenMain.LOGGER.info("getting there");
                    for (Object entrypoint : entrypoints) {
                        var map = new Object2ObjectOpenHashMap<Class<?>, ResourceLocation>();

                        FrozenMain.LOGGER.info("GETTING CLOSER");
                        if (type.isAssignableFrom(entrypoint.getClass())) {
                            var phase = map.getOrDefault(type, Event.DEFAULT_PHASE);
                            event.register(phase, (T) entrypoint);
                            FrozenMain.LOGGER.info("listener registered");
                        }
                    }

                    break;
                }
            }
        }
    }

    public static <T> Event<T> createCommonEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        var event = EventFactory.createArrayBacked(type, invokerFactory);

        FrozenMain.LOGGER.info("the events are creating");
        for (var eventType : EventType.VALUES) {
            FrozenMain.LOGGER.info("the enum is recognized");
            if (eventType.listener().isAssignableFrom(type)) {
                List<?> entrypoints = FabricLoader.getInstance().getEntrypoints(eventType.entrypoint(), eventType.listener());

                FrozenMain.LOGGER.info("getting there");
                for (Object entrypoint : entrypoints) {
                    var map = new Object2ObjectOpenHashMap<Class<?>, ResourceLocation>();

                    FrozenMain.LOGGER.info("GETTING CLOSER");
                    if (type.isAssignableFrom(entrypoint.getClass())) {
                        var phase = map.getOrDefault(type, Event.DEFAULT_PHASE);
                        event.register(phase, (T)entrypoint);
                        FrozenMain.LOGGER.info("listener registered");
                    }
                }

                break;
            }
        }

        return event;
    }
}
