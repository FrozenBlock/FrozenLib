package net.frozenblock.lib.event.api.events;

import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientBlockEntityLifecycleEvents {

	public static final Event<Load> BLOCK_ENTITY_LOAD = FrozenEvents.createEnvironmentEvent(Load.class, callbacks -> (blockEntity, level) -> {
		for (Load callback : callbacks) {
			callback.onLoad(blockEntity, level);
		}
	});

	public static final Event<Unload> BLOCK_ENTITY_UNLOAD = FrozenEvents.createEnvironmentEvent(Unload.class, callbacks -> (blockEntity, level) -> {
		for (Unload callback : callbacks) {
			callback.onUnload(blockEntity, level);
		}
	});

	@FunctionalInterface
	public interface Load {
		void onLoad(BlockEntity blockEntity, ClientLevel level);
	}

	@FunctionalInterface
	public interface Unload {
		void onUnload(BlockEntity blockEntity, ClientLevel level);
	}
}
