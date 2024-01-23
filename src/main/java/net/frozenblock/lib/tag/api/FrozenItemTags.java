/*
 * Copyright 2023-2024 FrozenBlock
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

import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public final class FrozenItemTags {

	private FrozenItemTags() {
		throw new UnsupportedOperationException("FrozenItemTags contains only static declarations.");
	}

	public static final TagKey<Item> NO_USE_GAME_EVENTS = bind("dont_emit_use_game_events");
	public static final TagKey<Item> HEAVY_ITEMS = bind("heavy_items");
	public static final TagKey<Item> ALWAYS_SAVE_COOLDOWNS = bind("always_save_cooldowns");

	@NotNull
	private static TagKey<Item> bind(String path) {
		return TagKey.create(Registries.ITEM, FrozenSharedConstants.id(path));
	}
}
