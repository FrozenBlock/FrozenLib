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
import net.minecraft.world.level.dimension.DimensionType;

@UtilityClass
public class FrozenLibDimensionTypeTags {
	public static final TagKey<DimensionType> OVERWORLD = of("overworld");
	public static final TagKey<DimensionType> NETHER = of("nether");
	public static final TagKey<DimensionType> END = of("end");

	private static TagKey<DimensionType> of(String path) {
		return TagKey.create(Registries.DIMENSION_TYPE, FrozenLibConstants.id(path));
	}
}
