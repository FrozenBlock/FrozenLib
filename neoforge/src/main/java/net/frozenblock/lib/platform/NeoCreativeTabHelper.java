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

package net.frozenblock.lib.platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.frozenblock.lib.platform.service.CreativeTabHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class NeoCreativeTabHelper implements CreativeTabHelper {

	private static void listen(ResourceKey<CreativeModeTab> tab, Consumer<BuildCreativeModeTabContentsEvent> listener) {
		ModLoadingContext.get().getActiveContainer().getEventBus().addListener((BuildCreativeModeTabContentsEvent event) -> {
			if (event.getTabKey().equals(tab)) listener.accept(event);
		});
	}

	@Override
	public void insert(ResourceKey<CreativeModeTab> tab, ItemLike item) {
		listen(tab, event -> event.accept(new ItemStack(item)));
	}

	@Override
	public void insertBefore(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility) {
		listen(tab, event -> event.insertBefore(comparedItem.asItem().getDefaultInstance(), new ItemStack(item), tabVisibility));
	}

	@Override
	public void insertAfter(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		ItemLike item,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		listen(tab, event -> event.insertAfter(comparedItem.asItem().getDefaultInstance(), new ItemStack(item), tabVisibility));
	}

	@Override
	public void addInstrument(
		ResourceKey<CreativeModeTab> tab,
		Item instrument, TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		listen(tab, event -> event.getParameters()
			.holders()
			.lookupOrThrow(Registries.INSTRUMENT)
			.get(tagKey)
			.ifPresent(
				named -> named.stream()
				.map(holder -> InstrumentItem.create(instrument, holder))
				.forEach(stack -> event.accept(stack, tabVisibility))
			)
		);
	}

	@Override
	public void addInstrumentBefore(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		listen(tab, event -> {
			final ItemStack existing = comparedItem.asItem().getDefaultInstance();
			final List<ItemStack> list = new ArrayList<>();
			event.getParameters()
				.holders()
				.lookupOrThrow(Registries.INSTRUMENT)
				.get(tagKey)
				.ifPresent(
					named -> named.stream()
					.map(holder -> InstrumentItem.create(instrument, holder))
					.forEach(list::add)
				);
			for (ItemStack stack : list) event.insertBefore(existing, stack, tabVisibility);
		});
	}

	@Override
	public void addInstrumentAfter(
		ResourceKey<CreativeModeTab> tab,
		ItemLike comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		CreativeModeTab.TabVisibility tabVisibility
	) {
		listen(tab, event -> {
			final ItemStack existing = comparedItem.asItem().getDefaultInstance();
			final List<ItemStack> list = new ArrayList<>();
			event.getParameters()
				.holders()
				.lookupOrThrow(Registries.INSTRUMENT)
				.get(tagKey)
				.ifPresent(
					named -> named.stream()
					.map(holder -> InstrumentItem.create(instrument, holder))
					.forEach(list::add)
				);
			Collections.reverse(list);
			for (ItemStack stack : list) event.insertAfter(existing, stack, tabVisibility);
		});
	}
}
