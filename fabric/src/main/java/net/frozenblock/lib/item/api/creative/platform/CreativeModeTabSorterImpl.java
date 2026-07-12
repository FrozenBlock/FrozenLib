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

package net.frozenblock.lib.item.api.creative.platform;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class CreativeModeTabSorterImpl {

	public static void insert(ResourceKey<CreativeModeTab> tab, ItemLike item) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.accept(new ItemStack(item)));
	}

	public static void insertBefore(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries ->
			entries.insertBefore(
				comparedItem,
				List.of(new ItemStack(item)),
				tabVisibility
			)
		);
	}

	public static void insertAfter(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries ->
			entries.insertAfter(
				comparedItem,
				List.of(new ItemStack(item)),
				tabVisibility
			)
		);
	}

	public static void addInstrument(
		ResourceKey<CreativeModeTab> tab,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.getContext()
			.holders()
			.lookupOrThrow(Registries.INSTRUMENT)
			.get(tagKey)
			.ifPresent(
				named -> named.stream()
				.map(holder -> InstrumentItem.create(instrument, holder))
				.forEach(stack -> entries.accept(stack, tabVisibility))
			)
		);
	}

	public static void addInstrumentBefore(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> {
			final List<ItemStack> list = new ArrayList<>();
			entries.getContext()
				.holders()
				.lookupOrThrow(Registries.INSTRUMENT)
				.get(tagKey)
				.ifPresent(
					named -> named.stream()
					.map(holder -> InstrumentItem.create(instrument, holder))
					.forEach(list::add)
				);
			entries.insertBefore(comparedItem, list, tabVisibility);
		});
	}

	public static void addInstrumentAfter(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> {
			final List<ItemStack> list = new ArrayList<>();
			entries.getContext()
				.holders()
				.lookupOrThrow(Registries.INSTRUMENT)
				.get(tagKey)
				.ifPresent(
					named -> named.stream()
					.map(holder -> InstrumentItem.create(instrument, holder))
					.forEach(list::add)
				);
			entries.insertAfter(comparedItem, list, tabVisibility);
		});
	}
}
