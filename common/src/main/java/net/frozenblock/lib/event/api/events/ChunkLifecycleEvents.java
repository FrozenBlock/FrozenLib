package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkLifecycleEvents {

	public static final Event<Load> CHUNK_LOAD = FrozenEvents.createEnvironmentEvent(Load.class, callbacks -> (level, chunk, newChunk) -> {
		for (Load callback : callbacks) {
			callback.onChunkLoad(level, chunk, newChunk);
		}
	});

	public static final Event<Unload> CHUNK_UNLOAD = FrozenEvents.createEnvironmentEvent(Unload.class, callbacks -> (level, chunk) -> {
		for (Unload callback : callbacks) {
			callback.onChunkUnload(level, chunk);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onChunkLoad(ServerLevel level, LevelChunk chunk, boolean newChunk);
	}

	@FunctionalInterface
	public interface Unload {
		void onChunkUnload(ServerLevel level, LevelChunk chunk);
	}
}
