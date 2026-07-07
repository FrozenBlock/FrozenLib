/*
 * Copyright (C) 2025-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.feature.api.FrozenLibFeatureUtil;
import net.frozenblock.lib.levelgen.blockpredicates.impl.FrozenLibBlockPredicateTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class TouchingBlockPredicate implements BlockPredicate {
	public static final MapCodec<TouchingBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPredicate.CODEC.fieldOf("block_predicate").forGetter(config -> config.blockPredicate),
		Codec.BOOL.fieldOf("all_must_match").forGetter(config -> config.allMustMatch)
	).apply(instance, TouchingBlockPredicate::new));
	private final BlockPredicate blockPredicate;
	private final boolean allMustMatch;

	public TouchingBlockPredicate(BlockPredicate blockPredicate, boolean allMustMatch) {
		this.blockPredicate = blockPredicate;
		this.allMustMatch = allMustMatch;
	}

	public static TouchingBlockPredicate exposedTo(BlockPredicate blockPredicate) {
		return new TouchingBlockPredicate(blockPredicate, false);
	}

	public static TouchingBlockPredicate surroundedBy(BlockPredicate blockPredicate) {
		return new TouchingBlockPredicate(blockPredicate, true);
	}

	public static TouchingBlockPredicate exposed() {
		return exposedTo(BlockPredicate.replaceable());
	}

	public static TouchingBlockPredicate exposedToWater() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.WATER));
	}

	public static TouchingBlockPredicate exposedToLava() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.LAVA));
	}

	public static TouchingBlockPredicate exposedToAir() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.AIR));
	}

	@Override
	public BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.TOUCHING.get();
	}

	@Override
	public boolean test(LevelAccessor level, BlockPos pos) {
		return FrozenLibFeatureUtil.matchesConditionsTouching(level, pos, this.allMustMatch, this.blockPredicate);
	}
}
