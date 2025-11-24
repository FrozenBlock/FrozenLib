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

package net.frozenblock.lib.worldgen.structure.impl;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.worldgen.structure.api.DataMarkerProcessableLegacySinglePoolElement;
import net.frozenblock.lib.worldgen.structure.api.DataMarkerProcessableSinglePoolElement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public final class FrozenStructurePoolElementTypes {
	public static final StructurePoolElementType<DataMarkerProcessableLegacySinglePoolElement> DATA_MARKER_PROCESSABLE_LEGACY_SINGLE = register(
		"data_marker_processable_legacy_single_pool_element",
		DataMarkerProcessableLegacySinglePoolElement.CODEC
	);
	public static final StructurePoolElementType<DataMarkerProcessableSinglePoolElement> DATA_MARKER_PROCESSABLE_SINGLE = register(
		"data_marker_processable_single_pool_element",
		DataMarkerProcessableSinglePoolElement.CODEC
	);

	public static void init() {
	}

	private static <P extends StructurePoolElement> StructurePoolElementType<P> register(String path, MapCodec<P> codec) {
		return Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, FrozenLibConstants.id(path), () -> codec);
	}

}
