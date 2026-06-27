package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.event.api.events.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@UtilityClass
public final class NeoClientLifecycleEventBridge {

	private static boolean clientStartedFired = false;

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> {
			if (!clientStartedFired) {
				clientStartedFired = true;
				ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(Minecraft.getInstance());
			}
		});

		NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event ->
			ClientConnectionEvents.DISCONNECT.invoker().onDisconnect(null, Minecraft.getInstance())
		);
	}
}
