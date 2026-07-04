package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;

@UtilityClass
public class FabricEntityLifecycleEventsBridge {
	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) ->
			EntityLifecycleEvents.ENTITY_LOAD.invoker().onEntityLoad(entity, level)
		);
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
			EntityLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(entity, level)
		);
	}
}
