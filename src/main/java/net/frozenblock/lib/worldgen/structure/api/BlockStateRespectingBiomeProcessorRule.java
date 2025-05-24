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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class BlockStateRespectingBiomeProcessorRule {
	public static final Passthrough DEFAULT_BLOCK_ENTITY_MODIFIER = Passthrough.INSTANCE;
	public static final Codec<BlockStateRespectingBiomeProcessorRule> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			TagKey.codec(Registries.BIOME).fieldOf("biomes").forGetter(rule -> rule.biomes),
			RuleTest.CODEC.fieldOf("input_predicate").forGetter(rule -> rule.inputPredicate),
			RuleTest.CODEC.fieldOf("location_predicate").forGetter(rule -> rule.locPredicate),
			PosRuleTest.CODEC.lenientOptionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter(rule -> rule.posPredicate),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output_block").forGetter(rule -> rule.outputBlock),
			RuleBlockEntityModifier.CODEC.lenientOptionalFieldOf("block_entity_modifier", DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter(rule -> rule.blockEntityModifier)
		).apply(instance, BlockStateRespectingBiomeProcessorRule::new)
	);
	private final TagKey<Biome> biomes;
	private final RuleTest inputPredicate;
	private final RuleTest locPredicate;
	private final PosRuleTest posPredicate;
	private final Block outputBlock;
	private final RuleBlockEntityModifier blockEntityModifier;

	public BlockStateRespectingBiomeProcessorRule(TagKey<Biome> biomes, RuleTest inputPredicate, RuleTest locationPredicate, Block outputBlock) {
		this(biomes, inputPredicate, locationPredicate, PosAlwaysTrueTest.INSTANCE, outputBlock);
	}

	public BlockStateRespectingBiomeProcessorRule(TagKey<Biome> biomes, RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, Block outputBlock) {
		this(biomes, inputPredicate, locationPredicate, positionPredicate, outputBlock, DEFAULT_BLOCK_ENTITY_MODIFIER);
	}

	public BlockStateRespectingBiomeProcessorRule(
		TagKey<Biome> biomes, RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, Block outputBlock, RuleBlockEntityModifier ruleBlockEntityModifier
	) {
		this.biomes = biomes;
		this.inputPredicate = inputPredicate;
		this.locPredicate = locationPredicate;
		this.posPredicate = positionPredicate;
		this.outputBlock = outputBlock;
		this.blockEntityModifier = ruleBlockEntityModifier;
	}

	public boolean test(BlockState input, BlockState location, BlockPos localPos, BlockPos absolutePos, BlockPos pivot, RandomSource random) {
		return this.inputPredicate.test(input, random) && this.locPredicate.test(location, random) && this.posPredicate.test(localPos, absolutePos, pivot, random);
	}

	public boolean doesBiomeMatch(Holder<Biome> biome) {
		return biome.is(this.biomes);
	}

	public BlockState getOutputState(BlockState inputState) {
		BlockState outputState = this.outputBlock.withPropertiesOf(inputState);
		return outputState.isAir() && inputState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : outputState;
	}

	@Nullable
	public CompoundTag getOutputTag(RandomSource random, @Nullable CompoundTag nbt) {
		return this.blockEntityModifier.apply(random, nbt);
	}
}
