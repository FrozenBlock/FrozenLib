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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.worldgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructurePlacement.class)
public class StructurePlacementMixin implements StructureSetAndPlacementInterface {

	@Unique
	private final List<Supplier<Boolean>> frozenLib$generationConditions = new ArrayList<>();
	@Unique
	private final List<Pair<Holder<StructureSet>, Integer>> frozenLib$addedExclusions = new ArrayList<>();

	@Unique
	@Override
	public synchronized void frozenLib$addGenerationConditions(List<Supplier<Boolean>> generationConditions) {
		this.frozenLib$generationConditions.clear();
		this.frozenLib$generationConditions.addAll(generationConditions);
	}

	@Unique
	@Override
	public synchronized List<Supplier<Boolean>> frozenLib$getGenerationConditions() {
		return this.frozenLib$generationConditions;
	}

	@Unique
	@Override
	public synchronized void frozenLib$addExclusions(@NotNull List<Pair<Identifier, Integer>> exclusions, HolderLookup.RegistryLookup<StructureSet> structureSetRegistryLookup) {
		this.frozenLib$addedExclusions.clear();
		exclusions.forEach(pair -> {
			structureSetRegistryLookup.get(ResourceKey.create(Registries.STRUCTURE_SET, pair.getFirst())).ifPresent(structureSet -> {
				this.frozenLib$addedExclusions.add(Pair.of(structureSet, pair.getSecond()));
			});
		});
	}

	@Inject(method = "isStructureChunk", at = @At("HEAD"), cancellable = true)
	public void frozenLib$checkPlacementConditions(ChunkGeneratorStructureState chunkGeneratorStructureState, int i, int j, CallbackInfoReturnable<Boolean> info) {
		if (!this.frozenLib$generationConditions.isEmpty()) {
			for (Supplier<Boolean> generationCondition : this.frozenLib$generationConditions) {
				if (!generationCondition.get()) info.setReturnValue(false);
				return;
			}
		}
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
