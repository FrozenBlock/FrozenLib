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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.debug.client.api.DebugRendererEvents;
import net.frozenblock.lib.debug.client.renderer.ImprovedGameEventListenerRenderer;
import net.frozenblock.lib.debug.client.renderer.ImprovedGoalSelectorDebugRenderer;
import net.frozenblock.lib.debug.client.renderer.WindDebugRenderer;
import net.frozenblock.lib.debug.client.renderer.WindDisturbanceDebugRenderer;
import net.frozenblock.lib.debug.networking.GoalDebugRemovePayload;
import net.frozenblock.lib.debug.networking.ImprovedGameEventDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGameEventListenerDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGoalDebugPayload;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class DebugRenderManager {
	public static Map<DebugRendererHolder, ResourceLocation> DEBUG_RENDERER_HOLDERS = new Object2ObjectLinkedOpenHashMap<>();
	private static final List<Runnable> ON_CLEAR_RUNNABLES = new ArrayList<>();

	public static void registerRenderer(ResourceLocation location, DebugRendererHolder.RenderInterface renderInterface) {
		if (location == null) throw new IllegalArgumentException("ResourceLocation cannot be null!");
		DEBUG_RENDERER_HOLDERS.put(new DebugRendererHolder(renderInterface), location);
		Stream<Map.Entry<DebugRendererHolder, ResourceLocation>> entries = DEBUG_RENDERER_HOLDERS.entrySet().stream()
			.sorted(Comparator.comparing(value ->  value.getValue().getPath()));

		Map<DebugRendererHolder, ResourceLocation> newRenderHolders = new Object2ObjectLinkedOpenHashMap<>();
		entries.forEach(entry -> newRenderHolders.put(entry.getKey(), entry.getValue()));

		DEBUG_RENDERER_HOLDERS = newRenderHolders;
	}

	public static void init() {
		DebugRendererEvents.DEBUG_RENDERERS_CREATED.register(client -> {
			ImprovedGoalSelectorDebugRenderer improvedGoalSelectorRenderer = new ImprovedGoalSelectorDebugRenderer(client);

			ClientPlayNetworking.registerGlobalReceiver(ImprovedGoalDebugPayload.PACKET_TYPE, (packet, ctx, sender) -> {
				Entity entity = client.level.getEntity(packet.entityId());
				if (entity != null) {
					improvedGoalSelectorRenderer.addGoalSelector(
						entity,
						packet.goals()
					);
				}
			});

			ClientPlayNetworking.registerGlobalReceiver(GoalDebugRemovePayload.PACKET_TYPE, (packet, ctx, sender) -> {
				improvedGoalSelectorRenderer.removeGoalSelector(packet.entityId());
			});

			addClearRunnable(improvedGoalSelectorRenderer::clear);

			registerRenderer(FrozenSharedConstants.id("goal"), improvedGoalSelectorRenderer::render);
		});

		DebugRendererEvents.DEBUG_RENDERERS_CREATED.register(client -> {
			ImprovedGameEventListenerRenderer improvedGameEventRenderer = new ImprovedGameEventListenerRenderer(client);

			ClientPlayNetworking.registerGlobalReceiver(ImprovedGameEventListenerDebugPayload.PACKET_TYPE, (packet, ctx, sender) -> {
				improvedGameEventRenderer.trackListener(
					packet.listenerPos(),
					packet.listenerRange()
				);
			});

			ClientPlayNetworking.registerGlobalReceiver(ImprovedGameEventDebugPayload.PACKET_TYPE, (packet, ctx, sender) -> {
				improvedGameEventRenderer.trackGameEvent(
					packet.gameEventType(),
					packet.pos()
				);
			});

			ClientTickEvents.START_WORLD_TICK.register(clientLevel -> {
				if (FrozenLibConfig.IS_DEBUG) {
					improvedGameEventRenderer.tick();
				}
			});

			registerRenderer(FrozenSharedConstants.id("game_event"), improvedGameEventRenderer::render);
		});

		DebugRendererEvents.DEBUG_RENDERERS_CREATED.register(client -> {
			WindDebugRenderer windDebugRenderer = new WindDebugRenderer(client);

			ClientTickEvents.START_WORLD_TICK.register(clientLevel -> {
				if (FrozenLibConfig.IS_DEBUG) {
					windDebugRenderer.tick();
					ClientWindManager.clearAccessedPositions();
				}
			});

			ClientPlayNetworking.registerGlobalReceiver(WindAccessPacket.PACKET_TYPE, (packet, ctx, sender) -> {
				ClientWindManager.addAccessedPosition(packet.accessPos());
			});

			addClearRunnable(windDebugRenderer::clear);

			registerRenderer(FrozenSharedConstants.id("wind"), windDebugRenderer::render);
		});

		DebugRendererEvents.DEBUG_RENDERERS_CREATED.register(client -> {
			WindDisturbanceDebugRenderer windDisturbanceDebugRenderer = new WindDisturbanceDebugRenderer(client);

			ClientTickEvents.START_WORLD_TICK.register(clientLevel -> {
				if (FrozenLibConfig.IS_DEBUG) {
					windDisturbanceDebugRenderer.tick();
				}
			});

			addClearRunnable(windDisturbanceDebugRenderer::clear);

			registerRenderer(FrozenSharedConstants.id("wind_disturbance"), windDisturbanceDebugRenderer::render);
		});
	}

	public static void clearAdditionalRenderers() {
		ON_CLEAR_RUNNABLES.forEach(Runnable::run);
	}

	public static void addClearRunnable(Runnable runnable) {
		ON_CLEAR_RUNNABLES.add(runnable);
	}

	public static float PARTIAL_TICK;

	public static void updatePartialTick() {
		PARTIAL_TICK = Minecraft.getInstance().getDeltaFrameTime();
	}
}
