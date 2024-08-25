package net.frozenblock.lib.item.api;

import lombok.experimental.UtilityClass;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class FuelRegistry {

	public static final List<ItemFuelValue> ITEM_FUEL_VALUES = new ArrayList<>();
	public static final List<TagFuelValue> TAG_FUEL_VALUES = new ArrayList<>();

	public static void add(ItemLike item, int time) {
		ITEM_FUEL_VALUES.add(new ItemFuelValue(item, time));
	}

	public static void add(TagKey<Item> tag, int time) {
		TAG_FUEL_VALUES.add(new TagFuelValue(tag, time));
	}

	public record ItemFuelValue(ItemLike item, int time) {}

	public record TagFuelValue(TagKey<Item> tag, int time) {}
}
