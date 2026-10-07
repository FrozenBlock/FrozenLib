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

package net.frozenblock.lib.levelgen.structure.mixin.processor;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SerializableChunkData.class)
public class SerializableChunkDataMixin {

	@ModifyExpressionValue(
		method = "unpackStructureStart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/resources/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
		)
	)
	private static Identifier frozenLib$captureStructureStartId(
		Identifier original,
		@Share("frozenLib$identifier") LocalRef<Identifier> identifierRef
	) {
		identifierRef.set(original);
		return original;
	}

	@ModifyExpressionValue(
		method = "unpackStructureStart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/structure/StructureStart;load(Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePieceSerializationContext;Lnet/minecraft/nbt/CompoundTag;JLnet/minecraft/world/level/levelgen/structure/Structure;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
		)
	)
	private static StructureStart frozenLib$setStructureStartId(
		StructureStart structureStart,
		@Share("frozenLib$identifier") LocalRef<Identifier> identifierRef
	) {
		structureStart.frozenLib$setId(identifierRef.get());
		return structureStart;
	}
}
