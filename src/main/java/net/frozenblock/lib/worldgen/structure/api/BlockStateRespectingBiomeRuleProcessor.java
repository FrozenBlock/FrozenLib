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

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
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
import java.util.List;

public class BlockStateRespectingBiomeRuleProcessor extends StructureProcessor {
	public static final MapCodec<BlockStateRespectingBiomeRuleProcessor> CODEC = BlockStateRespectingBiomeProcessorRule.CODEC.listOf()
		.fieldOf("rules").xmap(BlockStateRespectingBiomeRuleProcessor::new, processor -> processor.rules);
	private final ImmutableList<BlockStateRespectingBiomeProcessorRule> rules;

	public BlockStateRespectingBiomeRuleProcessor(List<? extends BlockStateRespectingBiomeProcessorRule> rules) {
		this.rules = ImmutableList.copyOf(rules);
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo processBlock(
		@NotNull LevelReader world,
		@NotNull BlockPos pos,
		@NotNull BlockPos pivot,
		StructureTemplate.@NotNull StructureBlockInfo localBlockInfo,
		StructureTemplate.@NotNull StructureBlockInfo absoluteBlockInfo,
		@NotNull StructurePlaceSettings placementData
	) {
		BlockPos posInfo = absoluteBlockInfo.pos();
		RandomSource randomSource = RandomSource.create(Mth.getSeed(posInfo));
		BlockState blockState = world.getBlockState(posInfo);
		BlockState inputState = absoluteBlockInfo.state();

		for (BlockStateRespectingBiomeProcessorRule processorRule : this.rules) {
			if (!processorRule.test(inputState, blockState, localBlockInfo.pos(), absoluteBlockInfo.pos(), pivot, randomSource)) continue;
			if (!processorRule.doesBiomeMatch(world.getBiome(posInfo))) continue;

			return new StructureTemplate.StructureBlockInfo(
				absoluteBlockInfo.pos(), processorRule.getOutputState(inputState), processorRule.getOutputTag(randomSource, absoluteBlockInfo.nbt())
			);
		}

		return absoluteBlockInfo;
	}

	@Override
	protected @NotNull StructureProcessorType<?> getType() {
		return FrozenStructureProcessorTypes.BLOCK_STATE_RESPECTING_RULE_PROCESSOR;
	}
}
