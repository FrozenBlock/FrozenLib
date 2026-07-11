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

package net.frozenblock.lib.item.api.creative;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/**
 * A class used for adding items to {@link CreativeModeTab}s.
 * <p>
 * ITEMS MUST BE REGISTERED BEFORE THEY ARE ADDED HERE.
 */
@UtilityClass
public class CreativeModeTabSorter {

	public static void insert(
		ItemLike item,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (item == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.insert(tab, item);
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertBefore(ItemLike comparedItem, ItemLike item, ResourceKey<CreativeModeTab>... tabs) {
		insertBefore(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertBefore(
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || item == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.insertBefore(tab, comparedItem, item, tabVisibility);
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertBefore(
		ItemLike comparedItem,
		ItemLike item,
		String path,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || item == null) return;
		FrozenLibLogUtils.logError("EMPTY ITEM IN CREATIVE INVENTORY: " + path, item.asItem() == Items.AIR, null);
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.insertBefore(tab, comparedItem, item, tabVisibility);
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertAfter(
		ItemLike comparedItem,
		ItemLike item,
		ResourceKey<CreativeModeTab>... tabs
	) {
		insertAfter(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertAfter(
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || item == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.insertAfter(tab, comparedItem, item, tabVisibility);
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void insertAfter(
		ItemLike comparedItem,
		ItemLike item,
		String path,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || item == null) return;
		FrozenLibLogUtils.logError("EMPTY ITEM IN CREATIVE INVENTORY: " + path, item.asItem() == Items.AIR, null);
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.insertAfter(tab, comparedItem, item, tabVisibility);
		}
	}

	public static void addInstrument(
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility,
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.addInstrument(tab, instrument, tagKey, tabVisibility);
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
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.addInstrumentBefore(tab, comparedItem, instrument, tagKey, tabVisibility);
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
		ResourceKey<CreativeModeTab> ... tabs
	) {
		if (comparedItem == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			FrozenLibInitPlatformUtils.CREATIVE_TAB.addInstrumentAfter(tab, comparedItem, instrument, tagKey, tabVisibility);
		}
	}
}
