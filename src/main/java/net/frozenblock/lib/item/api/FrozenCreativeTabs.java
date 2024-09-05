/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.item.api;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

/**
 * A class used for adding items to {@link CreativeModeTab}s.
 * <p>
 * ITEMS MUST BE REGISTERED BEFORE THEY ARE ADDED HERE.
 */
@UtilityClass
public class FrozenCreativeTabs {

	public static void add(ItemLike item, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (item == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
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
	public static void addBefore(ItemLike comparedItem, ItemLike item, ResourceKey<CreativeModeTab>... tabs) {
		addBefore(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addBefore(
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || item == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> entries.addBefore(comparedItem, list, tabVisibility));
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addBefore(
		ItemLike comparedItem,
		ItemLike item,
		String path,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || item == null ) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				FrozenLogUtils.logError("EMPTY ITEM IN CREATIVE INVENTORY: " + path, stack.isEmpty(), null);
				entries.addBefore(comparedItem, list, tabVisibility);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(
		ItemLike comparedItem,
		ItemLike item,
		ResourceKey<CreativeModeTab>... tabs
	) {
		addAfter(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || item == null) {
			return;
		} else {
			item.asItem();
		}
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> entries.addAfter(comparedItem, list, tabVisibility));
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(
		ItemLike comparedItem,
		ItemLike item,
		String path,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || item == null) {
			return;
		} else {
			item.asItem();
		}
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			var stack = new ItemStack(item);
			stack.setCount(1);
			List<ItemStack> list = List.of(stack);
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				FrozenLogUtils.logError("EMPTY ITEM IN CREATIVE INVENTORY: " + path, stack.isEmpty(), null);
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}

	public static void addInstrument(
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				entries.getContext().holders().lookupOrThrow(Registries.INSTRUMENT).get(tagKey)
					.ifPresent(
						named -> named.stream()
							.map(holder -> InstrumentItem.create(instrument, holder))
							.forEach(itemStack -> entries.accept(itemStack, tabVisibility))
					);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param instrument	The instrument that is going to be added
	 */
	public static void addInstrumentBefore(
		ItemLike comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
				List<ItemStack> list = new ArrayList<>();
				entries.getContext().holders().lookupOrThrow(Registries.INSTRUMENT).get(tagKey)
					.ifPresent(
						named -> named.stream()
							.map(holder -> InstrumentItem.create(instrument, holder))
							.forEach(list::add)
					);
				entries.addBefore(comparedItem, list, tabVisibility);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param instrument	The instrument that is going to be added
	 */
	public static void addInstrumentAfter(
		Item comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> @NotNull ... tabs
	) {
		if (comparedItem == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				List<ItemStack> list = new ArrayList<>();
				entries.getContext().holders().lookupOrThrow(Registries.INSTRUMENT).get(tagKey)
					.ifPresent(
						named -> named.stream()
							.map(holder -> InstrumentItem.create(instrument, holder))
							.forEach(list::add)
					);
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}
}
