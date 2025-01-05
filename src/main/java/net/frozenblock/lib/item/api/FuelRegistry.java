/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

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
