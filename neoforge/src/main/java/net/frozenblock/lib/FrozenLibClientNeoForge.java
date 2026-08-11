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
import net.frozenblock.lib.event.impl.NeoEventBridge;
import net.frozenblock.lib.networking.impl.FrozenLibClientNetworking;
import net.frozenblock.lib.renderer.blockentity.platform.BlockEntityRendererRegistryImpl;
import net.frozenblock.lib.renderer.entity.platform.EntityRendererRegistryImpl;
import net.frozenblock.lib.renderer.hud.platform.HudElementRegistryImpl;
import net.frozenblock.lib.renderer.model.platform.ModelLayerRegistryImpl;
import net.frozenblock.lib.particle.client.api.platform.ParticleProviderRegistryImpl;
import net.frozenblock.lib.renderer.block.BuiltInBlockModelRegistry;
import net.frozenblock.lib.renderer.special.platform.SpecialModelRendererRegistryImpl;
import net.frozenblock.lib.resource.api.platform.ResourceLoaderHelperImpl;
import net.frozenblock.lib.resource.client.api.pack.FrozenLibFolderRepositorySource;
import net.frozenblock.lib.screenshake.api.client.ClientScreenShaker;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(value = FrozenLibConstants.MOD_ID, dist = Dist.CLIENT)
public final class FrozenLibClientNeoForge {

	public FrozenLibClientNeoForge(IEventBus modBus) {
		FrozenLibClient.quiltSetup();
		FrozenLibClient.init();

		FrozenLibClientNetworking.registerClientReceivers();

		NeoEventBridge.initClientModStage();

		modBus.addListener(AddClientReloadListenersEvent.class, ResourceLoaderHelperImpl::flushClientListeners);
		modBus.addListener(RegisterGuiLayersEvent.class, HudElementRegistryImpl::flush);
		modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, ModelLayerRegistryImpl::flush);
		modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, BlockEntityRendererRegistryImpl::flush);
		modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, EntityRendererRegistryImpl::flush);
		modBus.addListener(RegisterSpecialModelRendererEvent.class, SpecialModelRendererRegistryImpl::flush);
		modBus.addListener(RegisterParticleProvidersEvent.class, ParticleProviderRegistryImpl::flush);

		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
			ClientScreenShaker.reset()
		);

		modBus.addListener(RegisterBlockModelsEvent.class, event -> {
			BuiltInBlockModelRegistry.REGISTER.invoker().addBuiltInBlockModels(event.getBuilder());
		});

		modBus.addListener(AddPackFindersEvent.class, event -> {
			FrozenLibFolderRepositorySource.createDefaultSources(Minecraft.getInstance().directoryValidator()).forEach(event::addRepositorySource);
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

		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (container, parent) ->
				FrozenLibConfigGui.buildScreen(parent)
		);
	}
}
