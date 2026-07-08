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

package net.frozenblock.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.frozenblock.lib.command.client.FrozenLibClientCommand;
import net.frozenblock.lib.config.frozenlib_config.gui.FrozenLibConfigGui;
import net.frozenblock.lib.event.api.events.ClientTickEvents;
import net.frozenblock.lib.event.impl.NeoEventBridge;
import net.frozenblock.lib.networking.FrozenLibClientNetworking;
import net.frozenblock.lib.platform.hud.NeoHudElementHelper;
import net.frozenblock.lib.platform.model.NeoModelLayerHelper;
import net.frozenblock.lib.platform.particle.NeoParticleProviderRegistryHelper;
import net.frozenblock.lib.platform.renderer.NeoBlockEntityRendererHelper;
import net.frozenblock.lib.platform.renderer.NeoEntityRendererHelper;
import net.frozenblock.lib.platform.resource.NeoResourceLoaderHelper;
import net.frozenblock.lib.renderer.block.BuiltInBlockModelRegistry;
import net.frozenblock.lib.renderer.model.FrozenLibModelLayers;
import net.frozenblock.lib.screenshake.api.client.ClientScreenShaker;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FrozenLibConstants.MOD_ID, dist = Dist.CLIENT)
public final class FrozenLibClientNeoForge {

	public FrozenLibClientNeoForge(IEventBus modBus) {
		FrozenLibClient.quiltInit();
		FrozenLibClient.init();

		FrozenLibModelLayers.init();

		FrozenLibClientNetworking.registerClientReceivers();

		NeoEventBridge.initClientModStage();

		modBus.addListener(AddClientReloadListenersEvent.class, NeoResourceLoaderHelper::flushClientListeners);
		modBus.addListener(RegisterGuiLayersEvent.class, NeoHudElementHelper::flush);
		modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, NeoModelLayerHelper::flush);
		modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, NeoBlockEntityRendererHelper::flush);
		modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, NeoEntityRendererHelper::flush);
		modBus.addListener(RegisterParticleProvidersEvent.class, NeoParticleProviderRegistryHelper::flush);

		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			if (level == null) return;
			ClientScreenShaker.tick(minecraft, level);
		});

		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
			ClientScreenShaker.reset()
		);

		modBus.addListener(RegisterBlockModelsEvent.class, event -> {
			BuiltInBlockModelRegistry.REGISTER.invoker().addBuiltInBlockModels(event.getBuilder());
		});

		NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, event -> {
			FrozenLibClientCommand.register(
				(CommandDispatcher) event.getDispatcher(),
				string -> LiteralArgumentBuilder.literal(string),
				(string, type) -> RequiredArgumentBuilder.argument(string, type),
				message -> {
					final Minecraft minecraft = Minecraft.getInstance();
					minecraft.gui.hud.getChat().addClientSystemMessage(message);
					minecraft.getNarrator().saySystemChatQueued(message);
				});
			});

		ClientTickEvents.START_LEVEL_TICK.register(level -> WindManager.getOrCreate(level).tick(level));
		ClientWindUtil.init();

		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (container, parent) ->
				FrozenLibConfigGui.buildScreen(parent)
		);
	}
}
