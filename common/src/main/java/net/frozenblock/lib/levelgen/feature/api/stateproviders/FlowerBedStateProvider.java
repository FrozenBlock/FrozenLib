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

package net.frozenblock.lib.levelgen.feature.api.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Provides all {@link BlockState}s of {@link FlowerBedBlock}s.
 * @param block The {@link Block} to generate (in {@link Holder} form).
 * @param minSegment The minimum {@link FlowerBedBlock#AMOUNT segment amount}.
 * <p>
 * Defaults to 1.
 * @param maxSegment The maximum {@link FlowerBedBlock#AMOUNT segment amount}.
 */
public record FlowerBedStateProvider(Holder<Block> block, int minSegment, int maxSegment) implements BlockStateProvider, SegmentableBlockStateProvider {
	public static final MapCodec<FlowerBedStateProvider> CODEC = SegmentableBlockStateProvider.codec(FlowerBedStateProvider::new);

	public FlowerBedStateProvider(Block block, int minSegment, int maxSegment) {
		this(block.builtInRegistryHolder(), minSegment, maxSegment);
	}

	public FlowerBedStateProvider(Block block, int maxSegment) {
		this(block, 1, maxSegment);
	}

	public FlowerBedStateProvider(Block block) {
		this(block, 1, 4);
	}

	@Override
	public MapCodec<FlowerBedStateProvider> codec() {
		return CODEC;
	}

	@Override
	public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
		return this.getState(random).trySetValue(FlowerBedBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
	}

	@Override
	public IntegerProperty segmentAmountProperty() {
		return FlowerBedBlock.AMOUNT;
	}

	@Override
	public Holder<Block> block() {
		return this.block;
	}
}
