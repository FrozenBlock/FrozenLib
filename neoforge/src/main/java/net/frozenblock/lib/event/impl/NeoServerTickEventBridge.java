package net.frozenblock.lib.event.impl;

import net.frozenblock.lib.event.api.events.FrozenLibServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoServerTickEventBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ServerTickEvent.Pre.class, NeoServerTickEventBridge::onPreServerTick);
		NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, NeoServerTickEventBridge::onPostServerTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Pre.class, NeoServerTickEventBridge::onPreLevelTick);
		NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, NeoServerTickEventBridge::onPostLevelTick);
	}

	private static void onPreServerTick(ServerTickEvent.Pre event) {
		FrozenLibServerTickEvents.START_SERVER_TICK.invoker().onStartTick(event.getServer());
	}
	private static void onPostServerTick(ServerTickEvent.Post event) {
		FrozenLibServerTickEvents.END_SERVER_TICK.invoker().onEndTick(event.getServer());
	}
	private static void onPreLevelTick(LevelTickEvent.Pre event) {
		if (!event.getLevel().isClientSide()) {
			FrozenLibServerTickEvents.START_LEVEL_TICK.invoker().onStartTick((ServerLevel) event.getLevel());
		}
	}
	private static void onPostLevelTick(LevelTickEvent.Post event) {
		if (!event.getLevel().isClientSide()) {
			FrozenLibServerTickEvents.END_LEVEL_TICK.invoker().onEndTick((ServerLevel) event.getLevel());
		}
	}
}
