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
		final ItemStack stack = event.getItemStack();

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
