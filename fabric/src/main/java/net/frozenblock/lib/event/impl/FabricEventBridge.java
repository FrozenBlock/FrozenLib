package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class FabricEventBridge {
	public static void initModStage() {
		FabricLootTableEventBridge.init();
	}
}
