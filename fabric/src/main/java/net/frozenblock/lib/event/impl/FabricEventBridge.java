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

package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationPacketListenerImpl;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.frozenblock.lib.event.api.events.ConfigurationConnectionEvents;
import net.frozenblock.lib.event.api.events.LifecycleEvents;

@UtilityClass
public final class FabricEventBridge {
	public static void initModStage() {
		FabricLootTableEventBridge.init();
		FabricServerTickEventsBridge.init();
		FabricServerLevelEventsBridge.init();
		FabricEntityTrackingEventsBridge.init();
		FabricEntityLifecycleEventsBridge.init();
		FabricBlockEntityLifecycleEventsBridge.init();
		FabricChunkLifecycleEventsBridge.init();

		ServerLifecycleEvents.SERVER_STARTING.register(instance -> LifecycleEvents.SERVER_STARTING.invoker().onServerStarting(instance));
		ServerLifecycleEvents.SERVER_STARTED.register(instance -> LifecycleEvents.SERVER_STARTED.invoker().onServerStarted(instance));
		ServerLifecycleEvents.SERVER_STOPPING.register(instance -> LifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(instance));
		ServerLifecycleEvents.SERVER_STOPPED.register(instance -> LifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(instance));

		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) ->
			ConfigurationConnectionEvents.SERVER_CONFIGURE.invoker().onServerConfigure(
				handler, server,
				task -> ((FabricServerConfigurationPacketListenerImpl) handler).addTask(task)
			)
		);
	}

	public static void initClientModStage() {
		FabricClientLifecycleEventBridge.init();
		FabricClientTickEventsBridge.init();
		FabricClientLevelEventsBridge.init();
		FabricClientEntityLifecycleEventsBridge.init();
		FabricClientBlockEntityLifecycleEventsBridge.init();
		FabricClientChunkLifecycleEventsBridge.init();
	}
}
