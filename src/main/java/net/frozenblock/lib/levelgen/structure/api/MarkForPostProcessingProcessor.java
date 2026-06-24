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

package net.frozenblock.lib.levelgen.structure.api;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public record MarkForPostProcessingProcessor(RuleTest inputPredicate) implements StructureProcessor {
	public static final MapCodec<MarkForPostProcessingProcessor> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RuleTest.CODEC.fieldOf("input_predicate").forGetter(MarkForPostProcessingProcessor::inputPredicate)
	).apply(instance, MarkForPostProcessingProcessor::new));

	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		LevelReader level,
		BlockPos targetPosition,
		BlockPos referencePos,
		BlockPos templateRelativePos,
		StructureTemplate.StructureBlockInfo processedBlockInfo,
		StructurePlaceSettings settings
	) {
		final BlockPos currentPos = processedBlockInfo.pos();
		final RandomSource random = RandomSource.create(Mth.getSeed(currentPos));
		if (this.inputPredicate.test(processedBlockInfo.state(), currentPos, random)) level.getChunk(currentPos).markPosForPostProcessing(currentPos);
		return processedBlockInfo;
	}

	@Override
	public MapCodec<MarkForPostProcessingProcessor> codec() {
		return MAP_CODEC;
	}
}
