package net.frozenblock.lib.platform;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
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

public class FabricCreativeTabHelper implements CreativeTabHelper {

	@Override
	public void insert(ResourceKey<CreativeModeTab> tab, ItemLike item) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.accept(new ItemStack(item)));
	}

	@Override
	public void insertBefore(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.insertBefore(comparedItem, List.of(new ItemStack(item)), tabVisibility));
	}

	@Override
	public void insertAfter(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility) {
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.insertAfter(comparedItem, List.of(new ItemStack(item)), tabVisibility));
	}

	@Override
	public void addInstrument(ResourceKey<CreativeModeTab> tab, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility) {
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

	@Override
	public void addInstrumentBefore(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility) {
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

	@Override
	public void addInstrumentAfter(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility) {
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
