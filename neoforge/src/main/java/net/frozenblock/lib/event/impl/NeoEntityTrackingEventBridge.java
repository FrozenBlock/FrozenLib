package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@UtilityClass
public class NeoEntityTrackingEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(PlayerEvent.StartTracking.class, NeoEntityTrackingEventBridge::onStartTracking);
		NeoForge.EVENT_BUS.addListener(PlayerEvent.StopTracking.class, NeoEntityTrackingEventBridge::onStopTracking);
	}

	private static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			EntityTrackingEvents.START_TRACKING.invoker().onStartTracking(event.getTarget(), player);
		}
	}

	private static void onStopTracking(PlayerEvent.StopTracking event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			EntityTrackingEvents.STOP_TRACKING.invoker().onStopTracking(event.getTarget(), player);
		}
	}
}
