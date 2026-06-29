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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import net.frozenblock.lib.levelgen.structure.api.processor.StructureProcessorListAdditions;
import net.frozenblock.lib.levelgen.structure.impl.processor.ProcessorInjectionInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public class StructureStartMixin {

	@Shadow
	@Final
	private Structure structure;

	@Inject(
		method = "placeInChunk",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;getCenter()Lnet/minecraft/core/BlockPos;"
		)
	)
	public void frozenLib$placeInChunkA(
		WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, CallbackInfo info,
		@Share("frozenLib$additionalProcessors") LocalRef<List<StructureProcessor>> additionalProcessors
	) {
		additionalProcessors.set(
			StructureProcessorListAdditions.getAdditions(level.registryAccess(), this.structure)
				.map(StructureProcessorList::list)
				.orElse(List.of())
		);
	}

	@WrapOperation(
		method = "placeInChunk",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V"
		)
	)
	public void frozenLib$placeInChunkB(
		StructurePiece instance,
		WorldGenLevel level,
		StructureManager structureManager,
		ChunkGenerator chunkGenerator,
		RandomSource random,
		BoundingBox boundingBox,
		ChunkPos chunkPos,
		BlockPos pos,
		Operation<Void> original,
		@Share("frozenLib$additionalProcessors") LocalRef<List<StructureProcessor>> additionalProcessors
	) {
		if (instance instanceof ProcessorInjectionInterface processorInjectionInterface) {
			processorInjectionInterface.frozenLib$addProcessors(additionalProcessors.get());
		}
		original.call(instance, level, structureManager, chunkGenerator, random, boundingBox, chunkPos, pos);
	}
}
