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

package net.frozenblock.lib.worldgen.biome.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.worldgen.biome.impl.FrozenGrassColorModifier;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class FrozenGrassColorModifiers {
	private static final Map<Identifier, FrozenGrassColorModifier> NEW_GRASS_COLOR_MODIFIERS = new LinkedHashMap<>();

	public static void addGrassColorModifier(Identifier id, FrozenGrassColorModifier grassColorModifier) {
		NEW_GRASS_COLOR_MODIFIERS.put(id, grassColorModifier);
	}

	public static Optional<FrozenGrassColorModifier> getGrassColorModifier(@NotNull Identifier id) {
		return Optional.ofNullable(NEW_GRASS_COLOR_MODIFIERS.get(id));
	}
}
