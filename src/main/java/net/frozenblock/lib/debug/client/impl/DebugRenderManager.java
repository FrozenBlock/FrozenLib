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
