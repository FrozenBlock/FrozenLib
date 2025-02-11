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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import net.frozenblock.lib.worldgen.structure.impl.StructureAddExclusionInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructurePlacement.class)
public abstract class StructurePlacementMixin implements StructureAddExclusionInterface {

	@Unique
	private final List<Pair<Holder<StructureSet>, Integer>> frozenLib$addedExclusions = new ArrayList<>();

	@Unique
	@Override
	public synchronized void frozenLib$addExclusions(List<Pair<ResourceLocation, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSetRegistryLookup) {
		exclusions.forEach(pair -> {
			structureSetRegistryLookup.get(ResourceKey.create(Registries.STRUCTURE_SET, pair.getFirst())).ifPresent(structureSet -> {
				this.frozenLib$addedExclusions.add(Pair.of(structureSet, pair.getSecond()));
			});
		});
	}

	@ModifyReturnValue(
		method = "applyInteractionsWithOtherStructures",
		at = @At(value = "RETURN")
	)
	public boolean frozenLib$isPlacementForbidden(
		boolean original,
		ChunkGeneratorStructureState chunkGeneratorStructureState, int i, int j
	) {
		if (original && !this.frozenLib$addedExclusions.isEmpty()) {
			for (Pair<Holder<StructureSet>, Integer> pair : this.frozenLib$addedExclusions) {
				if (chunkGeneratorStructureState.hasStructureChunkInRange(pair.getFirst(), i, j, pair.getSecond())) {
					return false;
				}
			}
		}
		return original;
	}
}
