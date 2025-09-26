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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.api.StructureProcessorApi;
import net.frozenblock.lib.worldgen.structure.impl.InitialPieceProcessorInjectionInterface;
import net.frozenblock.lib.worldgen.structure.impl.StructureStartInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public class StructureStartMixin implements StructureStartInterface {

	@Unique
	@Nullable
	private ResourceLocation frozenLib$id;

	@ModifyExpressionValue(
		method = "loadStaticStart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/ResourceLocation;parse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
		)
	)
	private static ResourceLocation frozenLib$loadStaticStartA(
		ResourceLocation original,
		@Share("frozenLib$resourceLocation") LocalRef<ResourceLocation> resourceLocationRef
	) {
		resourceLocationRef.set(original);
		return original;
	}

	@ModifyExpressionValue(
		method = "loadStaticStart",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/levelgen/structure/Structure;Lnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
		)
	)
	private static StructureStart frozenLib$loadStaticStartB(
		StructureStart structureStart, @Share("frozenLib$resourceLocation") LocalRef<ResourceLocation> resourceLocationRef
	) {
		StructureStartInterface.class.cast(structureStart).frozenLib$setId(resourceLocationRef.get());
		return structureStart;
	}

	@Inject(
		method = "placeInChunk",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;getCenter()Lnet/minecraft/core/BlockPos;"
		)
	)
	public void frozenLib$placeInChunkA(
		WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, CallbackInfo info,
		@Share("frozenLib$additionalProcessors") LocalRef<List<StructureProcessor>> additionalProcessors
	) {
		additionalProcessors.set(
			StructureProcessorApi.getAdditionalProcessors(this.frozenLib$id)
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
		WorldGenLevel worldGenLevel,
		StructureManager structureManager,
		ChunkGenerator chunkGenerator,
		RandomSource randomSource,
		BoundingBox boundingBox,
		ChunkPos chunkPos,
		BlockPos blockPos,
		Operation<Void> original,
		@Share("frozenLib$additionalProcessors") LocalRef<List<StructureProcessor>> additionalProcessors
	) {
		if (instance instanceof InitialPieceProcessorInjectionInterface initialPieceProcessorInjectionInterface) {
			initialPieceProcessorInjectionInterface.frozenLib$addProcessors(additionalProcessors.get());
		}
		original.call(instance, worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockPos);
	}

	@Unique
	@Override
	public synchronized ResourceLocation frozenLib$getId() {
		return this.frozenLib$id;
	}

	@Unique
	@Override
	public synchronized void frozenLib$setId(ResourceLocation id) {
		this.frozenLib$id = id;
	}
}
