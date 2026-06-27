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

import net.frozenblock.lib.command.NeoFrozenLibCommand;
import net.frozenblock.lib.event.impl.NeoEventBridge;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.frozenblock.lib.platform.FrozenLibEarlyPlatformUtils;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.platform.data.NeoDataAttachmentHelper;
import net.frozenblock.lib.platform.networking.NeoNetworkingHelper;
import net.frozenblock.lib.platform.registry.NeoRegistryHelper;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.screenshake.api.ScreenShakes;
import net.frozenblock.lib.screenshake.api.client.ClientScreenShaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.minecraft.server.level.ServerLevel;

@Mod(FrozenLibConstants.MOD_ID)
public final class FrozenLibNeoForge {

	public FrozenLibNeoForge(IEventBus modBus) {
		modBus.addListener(NewRegistryEvent.class, NeoRegistryHelper::flushRegistries);
		modBus.addListener(DataPackRegistryEvent.NewRegistry.class, event -> {
			FrozenLibRegistries.init();
			NeoRegistryHelper.flushDynamicRegistries(event);
		});
		modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
			FrozenNetworking.registerNetworking();
			if (FrozenLibEarlyPlatformUtils.LOADER.isClient()) {
				FrozenClientNetworking.registerClientReceivers();
			}
 			((NeoNetworkingHelper) FrozenLibInitPlatformUtils.NETWORKING).flush(event.registrar("frozenlib"));
		});

		FrozenLibMain.init();
		NeoDataAttachmentHelper.register(modBus);
		NeoEventBridge.initModStage(modBus);

		ScreenShakes.init();

		// Register command dispatcher on the global event bus — RegisterCommandsEvent is fired on NeoForge.EVENT_BUS
		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event ->
			NeoFrozenLibCommand.register(event.getDispatcher())
		);

		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> {
			Level level = event.getLevel();
			if (!(level instanceof ServerLevel serverLevel)) return;
			ScreenShakes.tick(serverLevel, serverLevel);
			for (Entity entity : serverLevel.getAllEntities()) {
				if (entity.isRemoved()) continue;
				ScreenShakes.tick(serverLevel, entity);
			}
		});

		if (FMLEnvironment.getDist().isClient()) {
			NeoEventBridge.initClientModStage();

			NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> {
				Minecraft minecraft = Minecraft.getInstance();
				ClientLevel level = minecraft.level;
				if (level == null) return;
				ClientScreenShaker.tick(minecraft, level);
			});

			NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
				ClientScreenShaker.reset()
			);
		}
	}
}
