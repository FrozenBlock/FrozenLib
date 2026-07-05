package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.registry.api.PotionBrewingRegistry;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@UtilityClass
public class NeoPotionBrewingEventBridge {
	public static void init() {
		NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event ->
			PotionBrewingRegistry.BUILD.invoker().build(event.getBuilder())
		);
	}
}
