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
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.event.api.events.LifecycleEvents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@UtilityClass
@EventBusSubscriber(modid = FrozenLibConstants.MOD_ID)
public final class NeoEventBridge {

	public static void initModStage(IEventBus modBus) {
		NeoForge.EVENT_BUS.register(NeoEventBridge.class);
		NeoLootTableEventBridge.init();
		NeoServerTickEventBridge.init();
		NeoServerLevelEventBridge.init();
		NeoEntityTrackingEventBridge.init();
		NeoEntityLifecycleEventBridge.init();
		NeoChunkLifecycleEventBridge.init();
	}

	public static void initClientModStage() {
		NeoClientLifecycleEventBridge.init();
		NeoClientTickEventBridge.init();
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void event(ServerStartingEvent event) {
		LifecycleEvents.SERVER_STARTING.invoker().onServerStarting(event.getServer());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void event(ServerStartedEvent event) {
		LifecycleEvents.SERVER_STARTED.invoker().onServerStarted(event.getServer());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void event(ServerStoppingEvent event) {
		LifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(event.getServer());
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void event(ServerStoppedEvent event) {
		LifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(event.getServer());
	}
}
