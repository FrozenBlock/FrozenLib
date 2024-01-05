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

package net.frozenblock.lib.worldgen.vein.api.entrypoint;

import java.util.ArrayList;
import net.frozenblock.lib.worldgen.vein.impl.FrozenVeinType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public interface FrozenVeinTypeEntrypoint {

	void newCategories(ArrayList<FrozenVeinType> context);

	static FrozenVeinType createCategory(ResourceLocation key, BlockState ore, BlockState rawOreBlock, BlockState filler, int minY, int maxY) {
		return new FrozenVeinType(key, ore, rawOreBlock, filler, minY, maxY);
	}

}

