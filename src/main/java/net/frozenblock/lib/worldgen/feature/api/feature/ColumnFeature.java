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

package net.frozenblock.lib.worldgen.feature.api.feature;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.worldgen.feature.api.feature.config.ColumnFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ColumnFeature extends Feature<ColumnFeatureConfig> {

	public ColumnFeature(Codec<ColumnFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<ColumnFeatureConfig> context) {
		final BlockPos blockPos = context.origin();
		final WorldGenLevel level = context.level();
		final RandomSource random = level.getRandom();
		final ColumnFeatureConfig config = context.config();

		final int length = config.length().sample(random);
		final BlockPredicate replaceable = config.replaceable();
		final BlockStateProvider blockStateProvider = config.stateProvider();
		final Direction direction = config.direction();
		final boolean stopWhenEncounteringUnreplaceableBlock = config.stopWhenEncounteringUnreplaceableBlock();

		boolean generated = false;
		final BlockPos.MutableBlockPos mutable = blockPos.mutable();
		for (int step = 0; step < length; step++) {
			if (replaceable.test(level, mutable)) {
				generated = true;
				level.setBlock(mutable, blockStateProvider.getState(random, mutable), Block.UPDATE_ALL);
			} else if (stopWhenEncounteringUnreplaceableBlock) {
				return generated;
			}
			mutable.move(direction);
		}
		return generated;
	}

}
