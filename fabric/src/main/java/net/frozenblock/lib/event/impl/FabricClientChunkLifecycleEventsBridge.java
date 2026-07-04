package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.frozenblock.lib.event.api.events.ClientChunkLifecycleEvents;

@UtilityClass
public class FabricClientChunkLifecycleEventsBridge {
	public static void init() {
		ClientChunkEvents.CHUNK_LOAD.register((level, chunk) ->
			ClientChunkLifecycleEvents.CHUNK_LOAD.invoker().onChunkLoad(level, chunk)
		);
		ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
			ClientChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload(level, chunk)
		);
	}
}
