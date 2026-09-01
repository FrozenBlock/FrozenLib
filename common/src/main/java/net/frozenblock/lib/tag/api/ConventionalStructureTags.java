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
import net.minecraft.world.level.levelgen.structure.Structure;

@UtilityClass
public final class ConventionalStructureTags {
	public static final TagKey<Structure> HIDDEN_FROM_DISPLAYERS = bind("hidden_from_displayers");
	public static final TagKey<Structure> HIDDEN_FROM_LOCATOR_SELECTION = bind("hidden_from_locator_selection");

	private static TagKey<Structure> bind(String path) {
		return TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("c", path));
	}
}
