/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.item.api;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
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
public final class FrozenCreativeTabs {
	private FrozenCreativeTabs() {
		throw new UnsupportedOperationException("FrozenCreativeTabs only contains static declarations.");
	}

	public static void add(ItemLike item, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (item == null || item.asItem() == null) return;
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
	public static void addBefore(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || item == null || item.asItem() == null) return;
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
	public static void addBefore(ItemLike comparedItem, ItemLike item, String path, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || item == null || item.asItem() == null) return;
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
	public static void addAfter(ItemLike comparedItem, ItemLike item, ResourceKey<CreativeModeTab>... tabs) {
		addAfter(comparedItem, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param item	The item that is going to be added
	 */
	public static void addAfter(ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || item == null || item.asItem() == null) return;
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
	public static void addAfter(ItemLike comparedItem, ItemLike item, String path, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || item == null || item.asItem() == null) return;
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

	public static void addInstrument(Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
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
	public static void addInstrumentBefore(ItemLike comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
				List<ItemStack> list = new ArrayList<>();
				for (Holder<Instrument> holder : BuiltInRegistries.INSTRUMENT.getTagOrEmpty(tagKey)) {
					var stack = InstrumentItem.create(instrument, holder);
					stack.setCount(1);
					list.add(stack);
				}
				entries.addBefore(comparedItem, list, tabVisibility);
			});
		}
	}

	/**
	 * @param comparedItem	The item that the added item is compared to
	 * @param instrument	The instrument that is going to be added
	 */
	public static void addInstrumentAfter(Item comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, ResourceKey<CreativeModeTab> @NotNull ... tabs) {
		if (comparedItem == null || comparedItem.asItem() == null || instrument == null) return;
		for (ResourceKey<CreativeModeTab> tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				List<ItemStack> list = new ArrayList<>();
				for (Holder<Instrument> holder : BuiltInRegistries.INSTRUMENT.getTagOrEmpty(tagKey)) {
					var stack = InstrumentItem.create(instrument, holder);
					stack.setCount(1);
					list.add(stack);
				}
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}
}
