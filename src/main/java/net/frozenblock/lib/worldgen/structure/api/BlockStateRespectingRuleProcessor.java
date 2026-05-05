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

package net.frozenblock.lib.worldgen.structure.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockStateRespectingRuleProcessor implements StructureProcessor {
	public static final MapCodec<BlockStateRespectingRuleProcessor> MAP_CODEC = BlockStateRespectingProcessorRule.CODEC.listOf()
		.fieldOf("rules").xmap(BlockStateRespectingRuleProcessor::new, processor -> processor.rules);
	private final ImmutableList<BlockStateRespectingProcessorRule> rules;

	public BlockStateRespectingRuleProcessor(List<? extends BlockStateRespectingProcessorRule> rules) {
		this.rules = ImmutableList.copyOf(rules);
	}

	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		LevelReader level,
		BlockPos targetPosition,
		BlockPos referencePos,
		BlockPos templateRelativePos,
		StructureTemplate.StructureBlockInfo processedBlockInfo,
		StructurePlaceSettings settings
	) {
		final RandomSource random = RandomSource.create(Mth.getSeed(processedBlockInfo.pos()));

		for (BlockStateRespectingProcessorRule rule : this.rules) {
			if (rule.test(level, processedBlockInfo.state(), templateRelativePos, processedBlockInfo.pos(), referencePos, random)) {
				return new StructureTemplate.StructureBlockInfo(
					processedBlockInfo.pos(), rule.getOutputState(processedBlockInfo.state()), rule.getOutputTag(random, processedBlockInfo.nbt())
				);
			}
		}

		return processedBlockInfo;
	}

	@Override
	public MapCodec<BlockStateRespectingRuleProcessor> codec() {
		return MAP_CODEC;
	}
}
