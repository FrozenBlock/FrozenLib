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
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosAlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class WeightedProcessorRule {
	public static final Codec<WeightedProcessorRule> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			RuleTest.CODEC.fieldOf("input_predicate").forGetter(rule -> rule.inputPredicate),
			RuleTest.CODEC.fieldOf("location_predicate").forGetter(rule -> rule.locPredicate),
			PosRuleTest.CODEC.lenientOptionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter(rule -> rule.posPredicate),
			WeightedRandomList.codec(WeightedEntry.Wrapper.codec(BlockState.CODEC)).fieldOf("output_states").forGetter(rule -> rule.outputStates)
		).apply(instance, WeightedProcessorRule::new)
	);
	private final RuleTest inputPredicate;
	private final RuleTest locPredicate;
	private final PosRuleTest posPredicate;
	private final WeightedRandomList<WeightedEntry.Wrapper<BlockState>> outputStates;

	public WeightedProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, WeightedRandomList<WeightedEntry.Wrapper<BlockState>> states) {
		this(inputPredicate, locationPredicate, PosAlwaysTrueTest.INSTANCE, states);
	}

	public WeightedProcessorRule(
		RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, WeightedRandomList<WeightedEntry.Wrapper<BlockState>> states
	) {
		this.inputPredicate = inputPredicate;
		this.locPredicate = locationPredicate;
		this.posPredicate = positionPredicate;
		this.outputStates = states;
	}

	public boolean test(BlockState input, BlockState location, BlockPos localPos, BlockPos absolutePos, BlockPos pivot, RandomSource random) {
		return this.inputPredicate.test(input, random) && this.locPredicate.test(location, random) && this.posPredicate.test(localPos, absolutePos, pivot, random);
	}

	public BlockState getOutputState(RandomSource random) {
		return this.outputStates.getRandom(random).orElseThrow().data();
	}
}
