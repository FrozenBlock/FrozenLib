package net.frozenblock.lib.worldgen.structure.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.jetbrains.annotations.Nullable;

public class BlockStateRespectingProcessorRule {
	public static final Passthrough DEFAULT_BLOCK_ENTITY_MODIFIER = Passthrough.INSTANCE;
	public static final Codec<BlockStateRespectingProcessorRule> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				RuleTest.CODEC.fieldOf("input_predicate").forGetter(rule -> rule.inputPredicate),
				RuleTest.CODEC.fieldOf("location_predicate").forGetter(rule -> rule.locPredicate),
				PosRuleTest.CODEC.lenientOptionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter(rule -> rule.posPredicate),
				BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output_block").forGetter(rule -> rule.outputBlock),
				RuleBlockEntityModifier.CODEC.lenientOptionalFieldOf("block_entity_modifier", DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter(rule -> rule.blockEntityModifier)
			)
			.apply(instance, BlockStateRespectingProcessorRule::new)
	);
	private final RuleTest inputPredicate;
	private final RuleTest locPredicate;
	private final PosRuleTest posPredicate;
	private final Block outputBlock;
	private final RuleBlockEntityModifier blockEntityModifier;

	public BlockStateRespectingProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, Block outputBlock) {
		this(inputPredicate, locationPredicate, PosAlwaysTrueTest.INSTANCE, outputBlock);
	}

	public BlockStateRespectingProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, Block outputBlock) {
		this(inputPredicate, locationPredicate, positionPredicate, outputBlock, DEFAULT_BLOCK_ENTITY_MODIFIER);
	}

	public BlockStateRespectingProcessorRule(
		RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, Block outputBlock, RuleBlockEntityModifier ruleBlockEntityModifier
	) {
		this.inputPredicate = inputPredicate;
		this.locPredicate = locationPredicate;
		this.posPredicate = positionPredicate;
		this.outputBlock = outputBlock;
		this.blockEntityModifier = ruleBlockEntityModifier;
	}

	public boolean test(BlockState input, BlockState location, BlockPos localPos, BlockPos absolutePos, BlockPos pivot, RandomSource random) {
		return this.inputPredicate.test(input, random) && this.locPredicate.test(location, random) && this.posPredicate.test(localPos, absolutePos, pivot, random);
	}

	public BlockState getOutputState(BlockState inputState) {
		return this.outputBlock.withPropertiesOf(inputState);
	}

	@Nullable
	public CompoundTag getOutputTag(RandomSource random, @Nullable CompoundTag nbt) {
		return this.blockEntityModifier.apply(random, nbt);
	}
}
