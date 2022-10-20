package net.frozenblock.lib.item.api;

import net.frozenblock.lib.item.impl.FrozenTabs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * A class used for adding items to {@link CreativeModeTab}s.
 * <p>
 * ITEMS MUST BE REGISTERED BEFORE THEY ARE ADDED HERE.
 */
public final class FrozenCreativeTabs {
	private FrozenCreativeTabs() {
		throw new UnsupportedOperationException("FrozenCreativeTabs only contains static declarations.");
	}

	public static void register(ItemLike item, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			((FrozenTabs) tab).getDisplayBuilder().accept(item);
		}
	}

	public static void registerInstrument(Item instrument, TagKey<Instrument> tagKey, CreativeModeTab.TabVisibility tabVisibility, CreativeModeTab... tabs) {
		for (CreativeModeTab tab : tabs) {
			CreativeModeTabs.generateInstrumentTypes(((FrozenTabs) tab).getDisplayBuilder(), instrument, tagKey, tabVisibility);
		}
	}
}
