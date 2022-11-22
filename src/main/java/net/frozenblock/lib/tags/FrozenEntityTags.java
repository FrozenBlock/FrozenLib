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

package net.frozenblock.lib.tags;

import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class FrozenEntityTags {

	private FrozenEntityTags() {
		throw new UnsupportedOperationException("FrozenEntityTags contains only static declarations.");
	}

	public static final TagKey<EntityType<?>> CREEPER_IGNORES = bind("creeper_ignores");

	private static TagKey<EntityType<?>> bind(String path) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, FrozenMain.id(path));
	}
}
