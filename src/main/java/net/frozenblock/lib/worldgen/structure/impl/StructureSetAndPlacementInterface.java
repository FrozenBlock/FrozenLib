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

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import java.util.List;
import java.util.function.Supplier;

public interface StructureSetAndPlacementInterface {

	void frozenLib$addGenerationConditions(List<Supplier<Boolean>> generationConditions);
	void frozenLib$flushGenerationConditions();

	void frozenLib$addExclusions(List<Pair<ResourceLocation, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSetRegistryLookup);
	void frozenLib$flushExclusions();
}
