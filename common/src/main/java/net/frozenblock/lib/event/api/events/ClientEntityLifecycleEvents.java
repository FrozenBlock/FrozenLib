package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class ClientEntityLifecycleEvents {

	public static final Event<Load> ENTITY_LOAD = FrozenEvents.createEnvironmentEvent(Load.class, callbacks -> (entity, level) -> {
		for (Load callback : callbacks) {
			callback.onEntityLoad(entity, level);
		}
	});

	public static final Event<Unload> ENTITY_UNLOAD = FrozenEvents.createEnvironmentEvent(Unload.class, callbacks -> (entity, level) -> {
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
