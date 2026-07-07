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

import java.util.function.Consumer;
import net.frozenblock.lib.event.api.events.ConfigurationConnectionEvents;
import net.frozenblock.lib.command.FrozenLibCommand;
import net.frozenblock.lib.event.impl.NeoEventBridge;
import net.frozenblock.lib.item.impl.NeoFuelRegistry;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.platform.attribute.NeoDefaultAttributeRegistryHelper;
import net.frozenblock.lib.platform.data.NeoDataAttachmentHelper;
import net.frozenblock.lib.platform.networking.NeoNetworkingHelper;
import net.frozenblock.lib.platform.registry.NeoRegistryHelper;
import net.frozenblock.lib.platform.resource.NeoResourceLoaderHelper;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.frozenblock.lib.wind.extension.WindManagerExtensionType;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.DelayedRegistry;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.NeoForgeDelayedRegistry;

@Mod(FrozenLibConstants.MOD_ID)
public final class FrozenLibNeoForge {

	public FrozenLibNeoForge(IEventBus modBus) {
		DelayedRegistry.setFactory(NeoForgeDelayedRegistry::new);

		modBus.addListener(NewRegistryEvent.class, NeoRegistryHelper::flushRegistries);
		modBus.addListener(DataPackRegistryEvent.NewRegistry.class, event -> {
			FrozenLibRegistries.init();
			NeoRegistryHelper.flushDynamicRegistries(event);
		});
		modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
			NeoNetworkingHelper neoNetworking = (NeoNetworkingHelper) FrozenLibInitPlatformUtils.NETWORKING;
			PayloadRegistrar registrar = event.registrar("frozenlib");
			neoNetworking.flush(registrar);
			neoNetworking.flushConfig(registrar);
		});
		modBus.addListener(AddPackFindersEvent.class, NeoResourceLoaderHelper::flushPackFinders);
		modBus.addListener(EntityAttributeCreationEvent.class, NeoDefaultAttributeRegistryHelper::flush);

		FrozenLibMain.preQuiltInit();
		FrozenLibMain.quiltInit();
		FrozenLibMain.init();

		NeoDataAttachmentHelper.register(modBus);
		NeoEventBridge.initModStage(modBus);

		modBus.addListener(RegisterConfigurationTasksEvent.class, event -> {
			var handler = (ServerConfigurationPacketListenerImpl) event.getListener();
			var server = ServerLifecycleHooks.getCurrentServer();
			if (server != null) {
				ConfigurationConnectionEvents.SERVER_CONFIGURE
					.invoker().onServerConfigure(handler, server, task -> {
						ICustomConfigurationTask neoTask = task instanceof ICustomConfigurationTask neo ? neo
							: new ICustomConfigurationTask() {
								@Override
								public void run(Consumer<CustomPacketPayload> send) {
									task.start(pkt -> {
										if (pkt instanceof ClientboundCustomPayloadPacket cpkt) {
											send.accept(cpkt.payload());
										}
									});
								}
								@Override
								public ConfigurationTask.Type type() {
									return task.type();
								}
							};
						event.register(neoTask);
					});
			}
		});

		ScreenShakes.init();
		WindManager.init();
		WindManagerExtensionType.init();
		WindDisturbances.init();
		WindDisturbanceType.init();
		NeoFuelRegistry.init();

		// some events need to use the NeoForge event bus
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event ->
			FrozenLibCommand.register(event.getDispatcher())
		);

		NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, NeoResourceLoaderHelper::flushServerListeners);

		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> {
			Level level = event.getLevel();
			if (!(level instanceof ServerLevel serverLevel)) return;
			ScreenShakes.tick(serverLevel, serverLevel);
			for (Entity entity : serverLevel.getAllEntities()) {
				if (entity.isRemoved()) continue;
				ScreenShakes.tick(serverLevel, entity);
			}
		});
	}
}
