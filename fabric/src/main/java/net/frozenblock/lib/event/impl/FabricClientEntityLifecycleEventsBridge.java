package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.frozenblock.lib.event.api.events.ClientEntityLifecycleEvents;

@UtilityClass
public class FabricClientEntityLifecycleEventsBridge {
	public static void init() {
		ClientEntityEvents.ENTITY_LOAD.register((entity, level) ->
			ClientEntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(entity, level)
		);
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
			ClientEntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(entity, level)
		);
	}
}
