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

package net.frozenblock.lib.levelgen.feature.api.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record ColumnFeature(
	BlockStateProvider state,
	BlockPredicate replaceable,
	IntProvider length,
	Direction direction,
	boolean stopAtUnreplaceableBlock
) implements Feature {
	public static final MapCodec<ColumnFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockStateProvider.CODEC.fieldOf("block_state").forGetter(ColumnFeature::state),
		BlockPredicate.CODEC.fieldOf("replaceable").forGetter(ColumnFeature::replaceable),
		IntProviders.NON_NEGATIVE_CODEC.fieldOf("length").forGetter(ColumnFeature::length),
		Direction.CODEC.fieldOf("direction").forGetter(ColumnFeature::direction),
		Codec.BOOL.lenientOptionalFieldOf("stop_at_unreplaceable_block", false).forGetter(ColumnFeature::stopAtUnreplaceableBlock)
	).apply(instance, ColumnFeature::new));

	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final int length = this.length.sample(random);
		boolean generated = false;
		final BlockPos.MutableBlockPos mutable = origin.mutable();
		for (int step = 0; step < length; step++) {
			if (this.replaceable.test(level, mutable)) {
				generated = true;
				level.setBlock(mutable, this.state.getState(level, random, mutable), Block.UPDATE_ALL);
			} else if (this.stopAtUnreplaceableBlock) {
				return generated;
			}
			mutable.move(this.direction);
		}
		return generated;
	}
}
