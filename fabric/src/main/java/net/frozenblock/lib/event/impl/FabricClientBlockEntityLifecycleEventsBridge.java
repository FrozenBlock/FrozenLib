package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.frozenblock.lib.event.api.events.ClientBlockEntityLifecycleEvents;

@UtilityClass
public class FabricClientBlockEntityLifecycleEventsBridge {
	public static void init() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) ->
			ClientBlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, level)
		);
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) ->
			ClientBlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, level)
		);
	}
}
