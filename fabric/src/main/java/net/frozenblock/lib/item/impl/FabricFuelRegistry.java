package net.frozenblock.lib.item.impl;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.frozenblock.lib.item.api.FuelRegistry;

@UtilityClass
public class FabricFuelRegistry {

	public static void init() {
		FuelValueEvents.BUILD.register((builder, context) -> {
			for (FuelRegistry.ItemFuelValue value : FuelRegistry.ITEM_FUEL_VALUES) builder.add(value.item(), value.time());
			for (FuelRegistry.TagFuelValue value : FuelRegistry.TAG_FUEL_VALUES) builder.add(value.tag(), value.time());
		});
	}
}
