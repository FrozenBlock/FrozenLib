/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api.block_predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.worldgen.feature.api.FrozenLibFeatureUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TouchingBlockPredicate implements BlockPredicate {
	public static final MapCodec<TouchingBlockPredicate> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			BlockPredicate.CODEC
				.fieldOf("block_predicate")
				.forGetter(config -> config.blockPredicate),
			Codec.BOOL
				.fieldOf("all_must_match")
				.forGetter(config -> config.allMustMatch)
		).apply(instance, TouchingBlockPredicate::new)
	);

	private final BlockPredicate blockPredicate;
	private final boolean allMustMatch;

	public TouchingBlockPredicate(
		@NotNull BlockPredicate blockPredicate,
		boolean allMustMatch
	) {
		this.blockPredicate = blockPredicate;
		this.allMustMatch = allMustMatch;
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull TouchingBlockPredicate exposedTo(BlockPredicate blockPredicate) {
		return new TouchingBlockPredicate(blockPredicate, false);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull TouchingBlockPredicate surroundedBy(BlockPredicate blockPredicate) {
		return new TouchingBlockPredicate(blockPredicate, true);
	}

	public static @NotNull TouchingBlockPredicate exposed() {
		return exposedTo(BlockPredicate.replaceable());
	}

	@Contract(" -> new")
	public static @NotNull TouchingBlockPredicate exposedToWater() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.WATER));
	}

	@Contract(" -> new")
	public static @NotNull TouchingBlockPredicate exposedToLava() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.LAVA));
	}

	@Contract(" -> new")
	public static @NotNull TouchingBlockPredicate exposedToAir() {
		return exposedTo(BlockPredicate.matchesBlocks(Blocks.AIR));
	}

	@Override
	public @NotNull BlockPredicateType<?> type() {
		return FrozenLibBlockPredicateTypes.TOUCHING;
	}

	@Override
	public boolean test(WorldGenLevel level, @NotNull BlockPos pos) {
		return FrozenLibFeatureUtils.matchesConditionsTouching(level, pos, this.allMustMatch, this.blockPredicate);
	}

}
