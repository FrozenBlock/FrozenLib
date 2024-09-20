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

package net.frozenblock.lib.worldgen.structure.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.frozenblock.lib.worldgen.structure.impl.FrozenStructureProcessorTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStateRespectingRuleProcessor extends StructureProcessor {
	public static final Codec<BlockStateRespectingRuleProcessor> CODEC = BlockStateRespectingProcessorRule.CODEC.listOf()
		.fieldOf("rules").xmap(BlockStateRespectingRuleProcessor::new, processor -> processor.rules).codec();
	private final ImmutableList<BlockStateRespectingProcessorRule> rules;

	public BlockStateRespectingRuleProcessor(List<? extends BlockStateRespectingProcessorRule> rules) {
		this.rules = ImmutableList.copyOf(rules);
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		LevelReader world,
		BlockPos pos,
		BlockPos pivot,
		StructureTemplate.StructureBlockInfo localBlockInfo,
		StructureTemplate.StructureBlockInfo absoluteBlockInfo,
		StructurePlaceSettings placementData
	) {
		BlockPos posInfo = absoluteBlockInfo.pos();
		RandomSource randomSource = RandomSource.create(Mth.getSeed(posInfo));
		BlockState blockState = world.getBlockState(posInfo);
		BlockState inputState = absoluteBlockInfo.state();

		for (BlockStateRespectingProcessorRule processorRule : this.rules) {
			if (processorRule.test(inputState, blockState, localBlockInfo.pos(), absoluteBlockInfo.pos(), pivot, randomSource)) {
				return new StructureTemplate.StructureBlockInfo(
					absoluteBlockInfo.pos(), processorRule.getOutputState(inputState), processorRule.getOutputTag(randomSource, absoluteBlockInfo.nbt())
				);
			}
		}

		return absoluteBlockInfo;
	}

	@Override
	protected @NotNull StructureProcessorType<?> getType() {
		return FrozenStructureProcessorTypes.BLOCK_STATE_RESPECTING_RULE_PROCESSOR;
	}
}
