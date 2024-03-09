/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.menu.api.Panoramas;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.particle.api.FrozenParticleTypes;
import net.frozenblock.lib.particle.impl.DebugPosParticle;
import net.frozenblock.lib.registry.api.client.FrozenClientRegistry;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.frozenblock.lib.sound.impl.block_sound_group.BlockSoundGroupManager;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client.ClientRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

public final class FrozenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FrozenClientRegistry.initRegistry();
		ModIntegrations.initializePreFreeze(); // Mod integrations must run after normal mod initialization

		// QUILT INIT
		ClientFreezer.onInitializeClient();
		ClientRegistrySync.registerHandlers();

		// CONTINUE FROZENLIB INIT
		registerClientEvents();
		FrozenClientNetworking.registerClientReceivers();

		// PARTICLES
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();

		particleRegistry.register(FrozenParticleTypes.DEBUG_POS, DebugPosParticle.Provider::new);

		Panoramas.addPanorama(new ResourceLocation("textures/gui/title/background/panorama"));

		var resourceLoader = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
		resourceLoader.registerReloadListener(BlockSoundGroupManager.INSTANCE);

		FrozenClientEntrypoint.EVENT.invoker().init(); // also includes dev init
	}

	private static void registerClientEvents() {
		ClientTickEvents.START_WORLD_TICK.register(ClientWindManager::tick);
		ClientTickEvents.START_WORLD_TICK.register(ScreenShaker::tick);
		ClientTickEvents.START_WORLD_TICK.register(level -> FlyBySoundHub.update(Minecraft.getInstance(), Minecraft.getInstance().getCameraEntity(), true));
		ClientTickEvents.START_CLIENT_TICK.register(client -> ClientWindManager.clearAndSwitchWindDisturbances());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ScreenShaker.clear();
			ClientWindManager.clearAllWindDisturbances();
		});
	}

}
