package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.neoforged.bus.api.IEventBus;

@UtilityClass
public final class NeoEventBridge {

	public static void initModStage(IEventBus modBus) {
		NeoLootTableEventBridge.init();
	}
}
