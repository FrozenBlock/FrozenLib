/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.frozenblock.lib.block.sound.impl.BlockSoundTypeManager;
import net.frozenblock.lib.cape.client.impl.ClientCapeData;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.core.client.api.PanoramaCommand;
import net.frozenblock.lib.debug.client.impl.DebugRenderManager;
import net.frozenblock.lib.debug.networking.StructureDebugRequestPayload;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.menu.api.PanoramaApi;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.registry.client.FrozenLibClientRegistries;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.sound.client.impl.FlyBySoundHub;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.lib.worldgen.structure.api.status.client.ClientStructureStatuses;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client.ClientRegistrySync;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

public final class FrozenLibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FrozenLibClientRegistries.initRegistry();
		ModIntegrations.initializePreFreeze(); // Mod integrations must run after normal mod initialization

		// QUILT INIT
		ClientFreezer.onInitializeClient();
		ClientRegistrySync.registerHandlers();

		// CONTINUE FROZENLIB INIT
		registerClientEvents();
		FrozenClientNetworking.registerClientReceivers();
		DebugRenderManager.init();

		PanoramaApi.addPanorama(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));
		ClientCapeData.init();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
			PanoramaCommand.register(dispatcher);
		});

		var resourceLoader = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
		resourceLoader.registerReloadListener(BlockSoundTypeManager.INSTANCE);

		FrozenClientEntrypoint.EVENT.invoker().init(); // also includes dev init
	}

	private static void registerClientEvents() {
		ClientTickEvents.START_WORLD_TICK.register(
			world -> {
				ClientWindManager.tick(world);
				ScreenShaker.tick(world);
				FlyBySoundHub.tick(Minecraft.getInstance(), Minecraft.getInstance().getCameraEntity(), true);
			}
		);
		ClientTickEvents.START_CLIENT_TICK.register(client -> ClientWindManager.clearAndSwitchWindDisturbances());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearClientListHolders());
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((minecraft, clientLevel) -> clearClientListHolders());
		ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			if (FrozenLibConfig.IS_DEBUG) world.sendPacketToServer(new ServerboundCustomPayloadPacket(new StructureDebugRequestPayload(chunk.getPos())));
		});
	}

	private static void clearClientListHolders() {
		ScreenShaker.clear();
		ClientWindManager.reset();
		ClientStructureStatuses.clearStructureStatuses();
	}

}
