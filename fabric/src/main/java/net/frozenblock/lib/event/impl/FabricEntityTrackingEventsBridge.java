package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

@UtilityClass
public class FabricEntityTrackingEventsBridge {
	public static void init() {
		EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) ->
			net.frozenblock.lib.event.api.events.EntityTrackingEvents.START_TRACKING.invoker().onStartTracking(trackedEntity, player)
		);
		EntityTrackingEvents.STOP_TRACKING.register((trackedEntity, player) ->
			net.frozenblock.lib.event.api.events.EntityTrackingEvents.STOP_TRACKING.invoker().onStopTracking(trackedEntity, player)
		);
	}
}
