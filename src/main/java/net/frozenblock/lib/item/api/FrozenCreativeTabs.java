/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.item.api;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.item.impl.FabricItemGroupAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class used for adding items to {@link CreativeModeTab}s.
 * <p>
 * ITEMS MUST BE REGISTERED BEFORE THEY ARE ADDED HERE.
 */
public final class FrozenCreativeTabs {
	private FrozenCreativeTabs() {
		throw new UnsupportedOperationException("FrozenCreativeTabs only contains static declarations.");
	}

	public static void add(ItemLike item, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				var stack = new ItemStack(item);
				stack.setCount(1);
				entries.accept(stack);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addBefore(ItemLike comparedItem, ItemLike item, CreativeModeTab... tabs) {
		addBefore(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addBefore(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				//if (((FabricItemGroupAccessor) entries).enabled(new ItemStack(comparedItem))) {
					entries.addBefore(comparedItem, list, tabVisibility);
				//}
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addBefore(ItemLike comparedItem, ItemLike item, String path, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				FrozenMain.error("EMPTY ITEM IN CREATIVE INVENTORY: " + path, stack.isEmpty());
				entries.addBefore(comparedItem, list, tabVisibility);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(ItemLike comparedItem, ItemLike item, CreativeModeTab... tabs) {
		addAfter(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(ItemLike comparedItem, ItemLike item, String path, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				FrozenMain.error("EMPTY ITEM IN CREATIVE INVENTORY: " + path, stack.isEmpty());
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}

	public static void addInstrument(Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				for (Holder<Instrument> holder : BuiltInRegistries.INSTRUMENT.getTagOrEmpty(tagKey)) {
					var stack = InstrumentItem.create(instrument, holder);
					stack.setCount(1);
					entries.accept(stack, tabVisibility);
				}
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param instrument	The instrument that is going to be added
	 */
	public static void addInstrumentBefore(Item comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				//if (((FabricItemGroupAccessor) entries).enabled(new ItemStack(comparedItem))) {
					List<ItemStack> list = new ArrayList<>();
					for (Holder<Instrument> holder : BuiltInRegistries.INSTRUMENT.getTagOrEmpty(tagKey)) {
						var stack = InstrumentItem.create(instrument, holder);
						stack.setCount(1);
						list.add(stack);
					}
					entries.addBefore(comparedItem, list, tabVisibility);
				//}
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param instrument	The instrument that is going to be added
	 */
	public static void addInstrumentAfter(Item comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				//if (((FabricItemGroupAccessor) entries).enabled(new ItemStack(comparedItem))) {
					List<ItemStack> list = new ArrayList<>();
					for (Holder<Instrument> holder : BuiltInRegistries.INSTRUMENT.getTagOrEmpty(tagKey)) {
						var stack = InstrumentItem.create(instrument, holder);
						stack.setCount(1);
						list.add(stack);
					}
					entries.addAfter(comparedItem, list, tabVisibility);
				//}
			});
		}
	}
}
