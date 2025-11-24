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
import java.util.Optional;
import net.frozenblock.lib.worldgen.feature.api.feature.config.ColumnWithDiskFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ColumnWithDiskFeature extends Feature<ColumnWithDiskFeatureConfig> {

	public ColumnWithDiskFeature(Codec<ColumnWithDiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<ColumnWithDiskFeatureConfig> context) {
		final ColumnWithDiskFeatureConfig config = context.config();
		final BlockPos pos = context.origin();
		final WorldGenLevel level = context.level();
		final BlockPos surfacePos = pos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1);
		final RandomSource random = level.getRandom();
		final int radius = config.radius().sample(random);

		boolean generated = false;

		// DISK
		placeDisk: {
			final Optional<Holder<Block>> diskOptional = config.diskBlocks().getRandomElement(random);
			if (diskOptional.isEmpty()) break placeDisk;

			final HolderSet<Block> replaceableBlocks = config.replaceableBlocks();

			final BlockPos.MutableBlockPos mutable = surfacePos.mutable();
			final BlockState diskState = diskOptional.get().value().defaultBlockState();
			final int originX = surfacePos.getX();
			final int originZ = surfacePos.getZ();
			for (int x = originX - radius; x <= originX + radius; x++) {
				for (int z = originZ - radius; z <= originZ + radius; z++) {
					final double distance = ((originX - x) * (originX - x) + ((originZ - z) * (originZ - z)));
					if (distance >= (radius * radius)) continue;

					mutable.set(x, level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
					final boolean fade = !mutable.closerThan(surfacePos, radius * 0.8D);
					if (!level.getBlockState(mutable).is(replaceableBlocks)) continue;

					generated = true;
					if (fade) {
						if (random.nextFloat() > 0.65F) level.setBlock(mutable, diskState, Block.UPDATE_CLIENTS);
					} else {
						level.setBlock(mutable, diskState, Block.UPDATE_CLIENTS);
					}
				}
			}
		}

		// COLUMN
		placeColumn: {
			final BlockState columnState = config.state();
			final BlockPos.MutableBlockPos mutable = pos.mutable();
			final int pillarHeight = config.height().sample(random);

			generated = placeAtPos(level, pos, mutable, columnState, pillarHeight) || generated;

			final int maxSurroundingPillarHeight = pillarHeight - 1;
			if (maxSurroundingPillarHeight <= 0) break placeColumn;

			for (Direction direction : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() >= config.surroundingPillarChance()) continue;
				generated = placeAtPos(level, pos.relative(direction), mutable, columnState, UniformInt.of(1, maxSurroundingPillarHeight).sample(random)) || generated;
			}
		}
		return generated;
	}

	private boolean placeAtPos(
		WorldGenLevel level,
		BlockPos pos,
		BlockPos.MutableBlockPos mutable,
		BlockState columnState,
		int height
	) {
		mutable.set(pos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) - 1));
		if (!level.getBlockState(mutable).getFluidState().isEmpty()) return false;

		boolean generated = false;
		for (int i = 0; i < height; i++) {
			final BlockState state = level.getBlockState(mutable.move(Direction.UP));
			if (!state.canBeReplaced()) continue;
				level.setBlock(mutable, columnState, Block.UPDATE_CLIENTS);
				generated = true;
		}

		return generated;
	}

}
