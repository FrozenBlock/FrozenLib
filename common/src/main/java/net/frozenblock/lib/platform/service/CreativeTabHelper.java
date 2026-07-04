package net.frozenblock.lib.platform.service;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface CreativeTabHelper {

	void insert(ResourceKey<CreativeModeTab> tab, ItemLike item);

	void insertBefore(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility);

	void insertAfter(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, ItemLike item, CreativeModeTab.TabVisibility tabVisibility);

	void addInstrument(ResourceKey<CreativeModeTab> tab, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility);

	void addInstrumentBefore(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility);

	void addInstrumentAfter(ResourceKey<CreativeModeTab> tab, ItemLike comparedItem, Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility);
}
