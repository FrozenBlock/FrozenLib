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

package net.frozenblock.lib.worldgen.structure.api;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.worldgen.structure.impl.FrozenStructureProcessorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class MarkForPostProcessingProcessor extends StructureProcessor {
	public static final MapCodec<MarkForPostProcessingProcessor> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			RuleTest.CODEC.fieldOf("input_predicate").forGetter(ruleProcessor -> ruleProcessor.inputPredicate)
		)
		.apply(instance, MarkForPostProcessingProcessor::new)
	);
	public final RuleTest inputPredicate;

	public MarkForPostProcessingProcessor(RuleTest inputPredicate) {
		this.inputPredicate = inputPredicate;
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		LevelReader level,
		BlockPos offset,
		BlockPos pos,
		StructureTemplate.StructureBlockInfo blockInfo,
		StructureTemplate.StructureBlockInfo relativeBlockInfo,
		StructurePlaceSettings settings
	) {
		final BlockPos currentPos = relativeBlockInfo.pos();
		final RandomSource random = RandomSource.create(Mth.getSeed(currentPos));
		if (this.inputPredicate.test(relativeBlockInfo.state(), random)) level.getChunk(currentPos).markPosForPostprocessing(currentPos);
		return relativeBlockInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return FrozenStructureProcessorTypes.MARK_FOR_POST_PROCESSING;
	}
}
