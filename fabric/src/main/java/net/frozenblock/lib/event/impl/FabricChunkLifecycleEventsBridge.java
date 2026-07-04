package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.frozenblock.lib.event.api.events.ChunkLifecycleEvents;

@UtilityClass
public class FabricChunkLifecycleEventsBridge {
	public static void init() {
		ServerChunkEvents.CHUNK_LOAD.register((level, chunk, generated) ->
			ChunkLifecycleEvents.CHUNK_LOAD.invoker().onChunkLoad(level, chunk, generated)
		);
		ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
			ChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload(level, chunk)
		);
	}
}
