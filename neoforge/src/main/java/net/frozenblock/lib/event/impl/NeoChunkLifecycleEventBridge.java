package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ChunkLifecycleEvents;
import net.frozenblock.lib.event.api.events.ClientChunkLifecycleEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

@UtilityClass
public class NeoChunkLifecycleEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ChunkEvent.Load.class, NeoChunkLifecycleEventBridge::onLoad);
		NeoForge.EVENT_BUS.addListener(ChunkEvent.Unload.class, NeoChunkLifecycleEventBridge::onUnload);
	}

	private static void onLoad(ChunkEvent.Load event) {
		final LevelAccessor level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			ChunkLifecycleEvents.CHUNK_LOAD.invoker().onChunkLoad(serverLevel, event.getChunk(), event.isNewChunk());
		} else if (level instanceof ClientLevel clientLevel) {
			ClientChunkLifecycleEvents.CHUNK_LOAD.invoker().onChunkLoad(clientLevel, event.getChunk());
		}
	}

	private static void onUnload(ChunkEvent.Unload event) {
		final LevelAccessor level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			ChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload(serverLevel, event.getChunk());
		} else if (level instanceof ClientLevel clientLevel) {
			ClientChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload(clientLevel, event.getChunk());
		}
	}
}
