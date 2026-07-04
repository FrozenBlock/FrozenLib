package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.frozenblock.lib.event.api.events.BlockEntityLifecycleEvents;

@UtilityClass
public class FabricBlockEntityLifecycleEventsBridge {
	public static void init() {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) ->
			BlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, level)
		);
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) ->
			BlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, level)
		);
	}
}
