/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.debug.client.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.ClientEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class DebugRendererEvents {

	public static final Event<DebugRenderersCreated> DEBUG_RENDERERS_CREATED = FrozenEvents.createEnvironmentEvent(
		DebugRenderersCreated.class,
		callbacks -> (client) -> {
			for (var callback : callbacks) {
				callback.onDebugRenderersCreated(client);
			}
		});

	@FunctionalInterface
	public interface DebugRenderersCreated extends ClientEventEntrypoint {
		void onDebugRenderersCreated(Minecraft client);
	}
}
