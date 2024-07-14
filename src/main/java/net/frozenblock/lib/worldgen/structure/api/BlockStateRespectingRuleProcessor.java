package net.frozenblock.lib.worldgen.structure.api;

import com.google.common.collect.ImmutableList;
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
	public static final MapCodec<BlockStateRespectingRuleProcessor> CODEC = BlockStateRespectingProcessorRule.CODEC.listOf()
		.fieldOf("rules").xmap(BlockStateRespectingRuleProcessor::new, processor -> processor.rules);
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
