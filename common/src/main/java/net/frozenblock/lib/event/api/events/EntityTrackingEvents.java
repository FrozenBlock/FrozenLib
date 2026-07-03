package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityTrackingEvents {

	public static final Event<StartTracking> START_TRACKING = FrozenEvents.createEnvironmentEvent(StartTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StartTracking callback : callbacks) {
			callback.onStartTracking(trackedEntity, player);
		}
	});

	public static final Event<StopTracking> STOP_TRACKING = FrozenEvents.createEnvironmentEvent(StopTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StopTracking callback : callbacks) {
			callback.onStopTracking(trackedEntity, player);
		}
	});

	@FunctionalInterface
	public interface StartTracking {
		void onStartTracking(Entity trackedEntity, ServerPlayer player);
	}

	@FunctionalInterface
	public interface StopTracking {
		void onStopTracking(Entity trackedEntity, ServerPlayer player);
	}
}
