/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.placementmodifier;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class FrozenPlacementModifiers {
	public static final PlacementModifierType<LowerHeightmapPlacement> ACCURATE_HEIGHTMAP = register("improved_heightmap", LowerHeightmapPlacement.CODEC);

	private static <P extends PlacementModifier> PlacementModifierType<P> register(String name, Codec<P> codec) {
		return Registry.register(Registry.PLACEMENT_MODIFIERS, FrozenMain.id(name), () -> codec);
	}

	public static void init() {

	}
}
