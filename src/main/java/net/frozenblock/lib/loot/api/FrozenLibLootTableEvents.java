/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.loot.api;

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * A class containing events related to loot tables.
 */
public class FrozenLibLootTableEvents {

	/**
	 * The event that is triggered when an item is generated in a {@link net.minecraft.world.Container}.
	 */
	public static final Event<ItemGeneratedInContainer> ON_ITEM_GENERATED_IN_CONTAINER = FrozenEvents.createEnvironmentEvent(
		ItemGeneratedInContainer.class,
		callbacks -> (server, player) -> {
			for (var callback : callbacks) callback.onItemGeneratedInContainer(server, player);
		});

	/**
	 * A functional interface representing an item generated in container event.
	 */
	@FunctionalInterface
	public interface ItemGeneratedInContainer extends CommonEventEntrypoint {
		/**
		 * Triggers the event when an item is generated in a {@link Container}.
		 * @param container The {@link Container} the {@link net.minecraft.world.level.storage.loot.LootTable} is placing an item into.
		 * @param stack The {@link ItemStack} being placed into the {@link Container}.
		 */
		void onItemGeneratedInContainer(Container container, ItemStack stack);
	}
}
