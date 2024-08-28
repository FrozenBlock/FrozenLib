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

package net.frozenblock.lib.debug.client.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frozenblock.lib.debug.client.ImprovedGameEventListenerRenderer;
import net.frozenblock.lib.debug.client.ImprovedGoalSelectorDebugRenderer;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class DebugRenderManager {
	public static final Map<DebugRendererHolder, ResourceLocation> DEBUG_RENDERER_HOLDERS = new Object2ObjectLinkedOpenHashMap<>();

	public static ImprovedGoalSelectorDebugRenderer improvedGoalSelectorRenderer;
	public static ImprovedGameEventListenerRenderer improvedGameEventListenerRenderer;

	public static void registerRenderer(ResourceLocation location, DebugRendererHolder.RenderInterface renderInterface) {
		DEBUG_RENDERER_HOLDERS.put(new DebugRendererHolder(renderInterface), location);
	}

	public static void init() {
		ClientTickEvents.START_WORLD_TICK.register(clientLevel -> {
			if (improvedGameEventListenerRenderer != null) {
				improvedGameEventListenerRenderer.tick();
			}
		});
	}
}
