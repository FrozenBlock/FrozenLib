/*
 * Copyright (C) 2024 FrozenBlock
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
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.api.StructureProcessorApi;
import net.frozenblock.lib.worldgen.structure.impl.PoolElementStructurePieceInterface;
import net.frozenblock.lib.worldgen.structure.impl.StructureStartInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureStart.class)
public class StructureStartMixin implements StructureStartInterface {

	@Shadow
	@Final
	private PiecesContainer pieceContainer;

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
		StructureStartInterface.class.cast(structureStart).frozenLib$addProcessorsFromId(resourceLocationRef.get());
		return structureStart;
	}

	@Override
	public void frozenLib$addProcessorsFromId(ResourceLocation id) {
		List<StructureProcessor> processorList = StructureProcessorApi.getAdditionalProcessors(id);
		if (!processorList.isEmpty()) {
			this.pieceContainer.pieces().forEach(structurePiece -> {
				if (structurePiece instanceof PoolElementStructurePieceInterface structurePieceInterface) {
					structurePieceInterface.frozenLib$addProcessors(processorList);
				}
			});
		}
	}
}
