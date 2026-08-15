/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

@UtilityClass
public class FrozenLibEntityTypeTags {
    public static final TagKey<EntityType<?>> CREEPER_IGNORES = bind("creeper_ignores");
	public static final TagKey<EntityType<?>> WARDEN_CANNOT_TARGET = bind("warden_cannot_target");
	public static final TagKey<EntityType<?>> SCARES_PIGLIN = bind("scares_piglin");
	public static final TagKey<EntityType<?>> BLAZES = bind("blazes");
	public static final TagKey<EntityType<?>> HOGLINS = bind("hoglins");
	public static final TagKey<EntityType<?>> GHOST_LIKE = bind("ghost_like");
	public static final TagKey<EntityType<?>> FIRE_LIKE = bind("fire_like");

    private static TagKey<EntityType<?>> bind(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, FrozenLibConstants.id(path));
    }
}
