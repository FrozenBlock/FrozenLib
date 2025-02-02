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

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.worldgen.feature.api.features.config.ChainFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class DownwardsChainFeature extends Feature<ChainFeatureConfig> {

	public DownwardsChainFeature(Codec<ChainFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<ChainFeatureConfig> context) {
		boolean bl = false;
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		RandomSource random = level.getRandom();
		BlockPos.MutableBlockPos mutable = blockPos.mutable();
		int bx = blockPos.getX();
		int bz = blockPos.getZ();
		int by = blockPos.getY();
		int height = -context.config().height().sample(random);

		for (int y = 0; y > height; y--) {
			BlockState blockState = level.getBlockState(mutable);
			if (context.config().replaceableBlocks().contains(blockState.getBlockHolder()) || blockState.isAir() || blockState.is(Blocks.WATER)) {
				bl = true;
				level.setBlock(
					mutable,
					Blocks.CHAIN.defaultBlockState().setValue(ChainBlock.WATERLOGGED, blockState.getFluidState().is(FluidTags.WATER)),
					Block.UPDATE_ALL
				);
			}
			mutable.set(bx, by + y, bz);
		}
		return bl;
	}

}
