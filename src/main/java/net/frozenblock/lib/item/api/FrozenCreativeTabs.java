package net.frozenblock.lib.item.api;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
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
				entries.accept(item);
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
			List<ItemStack> list = List.of(new ItemStack(item));
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
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
			List<ItemStack> list = List.of(new ItemStack(item));
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}

	public static void addInstrument(Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register((entries) -> {
				for (Holder<Instrument> holder : Registry.INSTRUMENT.getTagOrEmpty(tagKey)) {
					entries.accept(InstrumentItem.create(instrument, holder), tabVisibility);
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
				List<ItemStack> list = new ArrayList<>();
				for (Holder<Instrument> holder : Registry.INSTRUMENT.getTagOrEmpty(tagKey)) {
					list.add(InstrumentItem.create(instrument, holder));
				}
				entries.addBefore(comparedItem, list, tabVisibility);
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
				List<ItemStack> list = new ArrayList<>();
				for (Holder<Instrument> holder : Registry.INSTRUMENT.getTagOrEmpty(tagKey)) {
					list.add(InstrumentItem.create(instrument, holder));
				}
				entries.addAfter(comparedItem, list, tabVisibility);
			});
		}
	}
}
