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
