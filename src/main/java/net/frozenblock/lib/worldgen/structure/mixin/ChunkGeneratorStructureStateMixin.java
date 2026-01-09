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

package net.frozenblock.lib.worldgen.structure.mixin;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.frozenblock.lib.worldgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGeneratorStructureState.class)
public class ChunkGeneratorStructureStateMixin {

	@Shadow
	@Final
	private Map<Structure, List<StructurePlacement>> placementsForStructure;

	@Inject(method = "getPlacementsForStructure", at = @At("HEAD"), cancellable = true)
	public void frozenLib$optimizeRemovedStructureSearch(Holder<Structure> holder, CallbackInfoReturnable<List<StructurePlacement>> info) {
		final List<StructurePlacement> placements = this.placementsForStructure.get(holder.value());
		if (placements == null) return;

		for (StructurePlacement placement : placements) {
			if (!(placement instanceof StructureSetAndPlacementInterface structureSetAndPlacementInterface)) continue;

			final List<Supplier<Boolean>> supplierList = structureSetAndPlacementInterface.frozenLib$getGenerationConditions();
			if (supplierList == null || supplierList.isEmpty()) continue;

			if (supplierList.stream().anyMatch(supplier -> !supplier.get())) info.setReturnValue(List.of());
		}
	}

}
