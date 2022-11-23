/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.tag.api;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class FrozenItemTags {

	private FrozenItemTags() {
		throw new UnsupportedOperationException("FrozenItemTags contains only static declarations.");
	}

	public static final TagKey<Item> NO_USE_GAME_EVENTS = of("dont_emit_use_game_events");
	public static final TagKey<Item> HEAVY_ITEMS = of("heavy_items");

	private static TagKey<Item> of(String path) {
		return TagKey.create(Registry.ITEM_REGISTRY, FrozenMain.id(path));
	}
}
