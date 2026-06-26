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

package net.frozenblock.lib.levelgen.structure.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.levelgen.structure.impl.StructureSetAdditionInterface;
import net.frozenblock.lib.levelgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructureSet.class)
public abstract class StructureSetMixin implements StructureSetAndPlacementInterface, StructureSetAdditionInterface {

	@Shadow
	public abstract StructurePlacement placement();

	@Mutable
	@Shadow
	@Final
	private List<StructureSet.StructureSelectionEntry> structures;

	@Shadow
	public static StructureSet.StructureSelectionEntry entry(Holder<Structure> structure, int weight) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique
	@Override
	public synchronized void frozenLib$addOrModifyStructureSelectionEntry(Holder<Structure> structure, int weight) {
		final List<StructureSet.StructureSelectionEntry> entries = new ArrayList<>(this.structures);
		entries.removeIf(entry -> entry.structure().is(structure));
		entries.add(entry(structure, weight));
		this.structures = List.copyOf(entries);
	}

	@Unique
	@Override
	public synchronized void frozenLib$addGenerationConditions(List<Supplier<Boolean>> generationConditions) {
		if (this.placement() instanceof StructureSetAndPlacementInterface structureSetAndPlacementInterface) {
			structureSetAndPlacementInterface.frozenLib$addGenerationConditions(generationConditions);
		}
	}

	@Unique
	@Override
	public synchronized List<Supplier<Boolean>> frozenLib$getGenerationConditions() {
		if (this.placement() instanceof StructureSetAndPlacementInterface structureSetAndPlacementInterface) {
			return structureSetAndPlacementInterface.frozenLib$getGenerationConditions();
		}
		return List.of();
	}

	@Unique
	@Override
	public synchronized void frozenLib$addExclusions(List<Pair<Identifier, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSets) {
		if (this.placement() instanceof StructureSetAndPlacementInterface structureSetAndPlacementInterface) {
			structureSetAndPlacementInterface.frozenLib$addExclusions(exclusions, structureSets);
		}
	}
}
