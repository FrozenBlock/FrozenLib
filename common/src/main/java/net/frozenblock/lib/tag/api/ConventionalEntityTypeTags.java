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

package net.frozenblock.lib.tag.api;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

@UtilityClass
public final class ConventionalEntityTypeTags {
	public static final TagKey<EntityType<?>> BOSSES = bind("bosses");
	public static final TagKey<EntityType<?>> MINECARTS = bind("minecarts");
	public static final TagKey<EntityType<?>> BOATS = bind("boats");
	public static final TagKey<EntityType<?>> ITEM_FRAMES = bind("item_frames");
	public static final TagKey<EntityType<?>> CAPTURING_NOT_SUPPORTED = bind("capturing_not_supported");
	public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = bind("teleporting_not_supported");

	private static TagKey<EntityType<?>> bind(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", path));
	}
}
