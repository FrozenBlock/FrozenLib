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

package net.frozenblock.lib.worldgen.structure.mixin;

import com.mojang.datafixers.util.Pair;
import net.frozenblock.lib.worldgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.List;
import java.util.function.Supplier;

@Mixin(StructureSet.class)
public abstract class StructureSetMixin implements StructureSetAndPlacementInterface {

	@Shadow
	public abstract StructurePlacement placement();

	@Unique
	@Override
	public synchronized void frozenLib$addGenerationConditions(List<Supplier<Boolean>> generationConditions) {
		if (this.placement() instanceof StructureSetAndPlacementInterface addExclusionInterface) {
			addExclusionInterface.frozenLib$addGenerationConditions(generationConditions);
		}
	}

	@Unique
	@Override
	public synchronized void frozenLib$flushGenerationConditions() {
		if (this.placement() instanceof StructureSetAndPlacementInterface addExclusionInterface) {
			addExclusionInterface.frozenLib$flushGenerationConditions();
		}
	}

	@Unique
	@Override
	public synchronized void frozenLib$addExclusions(List<Pair<ResourceLocation, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSetRegistryLookup) {
		if (this.placement() instanceof StructureSetAndPlacementInterface addExclusionInterface) {
			addExclusionInterface.frozenLib$addExclusions(exclusions, structureSetRegistryLookup);
		}
	}

	@Unique
	@Override
	public synchronized void frozenLib$flushExclusions() {
		if (this.placement() instanceof StructureSetAndPlacementInterface addExclusionInterface) {
			addExclusionInterface.frozenLib$flushExclusions();
		}
	}
}
