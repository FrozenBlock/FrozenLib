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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

@Environment(EnvType.CLIENT)
public class ClientLevelEvents {

	/**
	 * Called after the client level has been changed.
	 *
	 * <p>The provided level is the new level. This event is not called when the level becomes {@code null}.
	 */
	public static final Event<AfterClientLevelChange> AFTER_CLIENT_LEVEL_CHANGE = EventRegistry.createEnvironmentEvent(AfterClientLevelChange.class, callbacks -> (client, level) -> {
		for (AfterClientLevelChange callback : callbacks) {
			callback.afterLevelChange(client, level);
		}
	});

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterClientLevelChange {
		void afterLevelChange(Minecraft client, ClientLevel level);
	}
}
