package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class ClientChunkLifecycleEvents {

	public static final Event<Load> CHUNK_LOAD = FrozenEvents.createEnvironmentEvent(Load.class, callbacks -> (level, chunk) -> {
		for (Load callback : callbacks) {
			callback.onChunkLoad(level, chunk);
		}
	});

	public static final Event<Unload> CHUNK_UNLOAD = FrozenEvents.createEnvironmentEvent(Unload.class, callbacks -> (level, chunk) -> {
		for (Unload callback : callbacks) {
			callback.onChunkUnload(level, chunk);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onChunkLoad(ClientLevel level, LevelChunk chunk);
	}

	@FunctionalInterface
	public interface Unload {
		void onChunkUnload(ClientLevel level, LevelChunk chunk);
	}
}
