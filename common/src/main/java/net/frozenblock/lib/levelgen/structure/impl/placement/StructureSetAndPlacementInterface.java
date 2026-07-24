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

package net.frozenblock.lib.levelgen.structure.impl.placement;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public interface StructureSetAndPlacementInterface {
	default void frozenLib$addGenerationConditions(List<Supplier<Boolean>> generationConditions) {
		throw new AssertionError();
	}

	default List<Supplier<Boolean>> frozenLib$getGenerationConditions() {
		throw new AssertionError();
	}

	default void frozenLib$addExclusions(List<Pair<Identifier, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSets) {
		throw new AssertionError();
	}
}
