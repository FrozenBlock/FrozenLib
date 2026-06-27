package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;

@UtilityClass
public final class FabricClientLifecycleEventBridge {

	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(mc ->
			net.frozenblock.lib.event.api.events.ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(mc)
		);
		ClientLifecycleEvents.CLIENT_STOPPING.register(mc ->
			net.frozenblock.lib.event.api.events.ClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(mc)
		);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
			ClientConnectionEvents.DISCONNECT.invoker().onDisconnect(handler, client)
		);
	}
}
