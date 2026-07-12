/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
		} else if (level.isClientSide()) {
			ClientChunkLifecycleEvents.CHUNK_LOAD.invoker().onChunkLoad((ClientLevel) level, event.getChunk());
		}
	}

	private static void onUnload(ChunkEvent.Unload event) {
		final LevelAccessor level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			ChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload(serverLevel, event.getChunk());
		} else if (level.isClientSide()) {
			ClientChunkLifecycleEvents.CHUNK_UNLOAD.invoker().onChunkUnload((ClientLevel) level, event.getChunk());
		}
	}
}
