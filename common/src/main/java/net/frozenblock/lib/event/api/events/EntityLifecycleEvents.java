package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class EntityLifecycleEvents {

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
		void onEntityLoad(Entity entity, ServerLevel level);
	}

	@FunctionalInterface
	public interface Unload {
		void onEntityUnload(Entity entity, ServerLevel level);
	}
}
