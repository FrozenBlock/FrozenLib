package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.frozenblock.lib.registry.api.PotionBrewingRegistry;

@UtilityClass
public class FabricPotionBrewingEventBridge {
	public static void init() {
		FabricPotionBrewingBuilder.BUILD.register(builder ->
			PotionBrewingRegistry.BUILD.invoker().build(builder)
		);
	}
}
