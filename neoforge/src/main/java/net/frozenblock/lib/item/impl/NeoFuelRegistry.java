package net.frozenblock.lib.item.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.item.api.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

@UtilityClass
public class NeoFuelRegistry {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(FurnaceFuelBurnTimeEvent.class, NeoFuelRegistry::onFurnaceFuelBurnTime);
	}

	private static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();

		for (FuelRegistry.ItemFuelValue value : FuelRegistry.ITEM_FUEL_VALUES) {
			if (stack.is(value.item().asItem())) {
				event.setBurnTime(value.time());
				return;
			}
		}

		for (FuelRegistry.TagFuelValue value : FuelRegistry.TAG_FUEL_VALUES) {
			if (stack.is(value.tag())) {
				event.setBurnTime(value.time());
				return;
			}
		}
	}
}
