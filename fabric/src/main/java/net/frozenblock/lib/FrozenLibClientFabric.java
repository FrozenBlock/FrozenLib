/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.frozenblock.lib.cape.client.api.ClientCapeUtil;
import net.frozenblock.lib.command.client.FrozenLibClientCommand;
import net.frozenblock.lib.config.v2.ConfigSerializer;
import net.frozenblock.lib.core.client.api.PanoramaCommand;
import net.frozenblock.lib.debug.client.gui.FrozenLibDebugScreenEntries;
import net.frozenblock.lib.entity.client.impl.spottingicon.SpottingIconHudElement;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.event.api.events.ClientLevelEvents;
import net.frozenblock.lib.event.api.events.ClientLifecycleEvents;
import net.frozenblock.lib.event.api.events.ClientTickEvents;
import net.frozenblock.lib.event.impl.FabricEventBridge;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.networking.FrozenClientNetworkingFabric;
import net.frozenblock.lib.particle.client.resource.FrozenLibParticleResources;
import net.frozenblock.lib.renderer.model.FrozenLibModelLayers;
import net.frozenblock.lib.resource_pack.api.client.FrozenLibModResourcePackApi;
import net.frozenblock.lib.screenshake.api.client.ClientScreenShaker;
import net.frozenblock.lib.sound.client.impl.FlyBySoundHub;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.minecraft.client.Minecraft;

public final class FrozenLibClientFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FabricEventBridge.initClientModStage();

		ClientLifecycleEvents.CLIENT_STARTED.register((_) -> {
			try {
				ConfigSerializer.saveConfigs(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		FrozenLibClient.preQuiltInit();
		ModIntegrations.initializePreFreeze(); // Mod integrations must run after normal mod initialization

		// QUILT INIT
		FrozenLibClient.quiltInit();

		// CONTINUE FROZENLIB INIT
		FrozenLibClient.init();

		registerClientEvents();
		FrozenClientNetworking.registerClientReceivers();
		FrozenClientNetworkingFabric.registerClientReceivers();
		ClientCapeUtil.init();

		FrozenLibParticleResources.init();
		FrozenLibModelLayers.init();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
			PanoramaCommand.register(dispatcher);
			FrozenLibClientCommand.register(dispatcher);
		});

		HudElementRegistry.attachElementAfter(
			VanillaHudElements.MISC_OVERLAYS,
			FrozenLibConstants.id("spotting_icons"),
			new SpottingIconHudElement()
		);

		FrozenLibModResourcePackApi.init();
		FrozenLibDebugScreenEntries.init();
		ClientWindUtil.init();

		FrozenClientEntrypoint.EVENT.invoker().init(); // also includes dev init
	}

	private static void registerClientEvents() {
		ClientTickEvents.START_LEVEL_TICK.register(
			level -> {
				final Minecraft minecraft = Minecraft.getInstance();
				WindManager.getOrCreate(level).tick(level);
				ClientScreenShaker.tick(minecraft, level);
				FlyBySoundHub.tick(minecraft, minecraft.getCameraEntity(), true);
			}
		);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearStaticClientData());
		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((minecraft, clientLevel) -> clearStaticClientData());
	}

	private static void clearStaticClientData() {
		ClientScreenShaker.reset();
	}
}
