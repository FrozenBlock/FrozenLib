package net.frozenblock.lib.event.impl;

import net.frozenblock.lib.event.api.events.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class NeoClientTickEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, NeoClientTickEventBridge::onPreClientTick);
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, NeoClientTickEventBridge::onPostClientTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Pre.class, NeoClientTickEventBridge::onPreLevelTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, NeoClientTickEventBridge::onPostLevelTick);
	}

	private static void onPreClientTick(ClientTickEvent.Pre event) {
		ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(Minecraft.getInstance());
	}

	private static void onPostClientTick(ClientTickEvent.Post event) {
		ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick(Minecraft.getInstance());
	}

	private static void onPreLevelTick(LevelTickEvent.Pre event) {
		if (event.getLevel().isClientSide()) {
			ClientTickEvents.START_LEVEL_TICK.invoker().onStartTick((ClientLevel) event.getLevel());
		}
	}

	private static void onPostLevelTick(LevelTickEvent.Post event) {
		if (event.getLevel().isClientSide()) {
			ClientTickEvents.END_LEVEL_TICK.invoker().onEndTick((ClientLevel) event.getLevel());
		}
	}
}
