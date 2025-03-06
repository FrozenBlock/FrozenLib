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
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class ColumnWithDiskFeature extends Feature<ColumnWithDiskFeatureConfig> {

	public ColumnWithDiskFeature(Codec<ColumnWithDiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<ColumnWithDiskFeatureConfig> context) {
		boolean generated = false;
		ColumnWithDiskFeatureConfig config = context.config();
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		BlockPos surfacePos = blockPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) - 1);
		RandomSource random = level.getRandom();
		int radius = config.radius().sample(random);
		Optional<Holder<Block>> diskOptional = config.diskBlocks().getRandomElement(random);
		// DISK
		if (diskOptional.isPresent()) {
			BlockPos.MutableBlockPos mutableDisk = surfacePos.mutable();
			BlockState disk = diskOptional.get().value().defaultBlockState();
			int bx = surfacePos.getX();
			int bz = surfacePos.getZ();
			for (int x = bx - radius; x <= bx + radius; x++) {
				for (int z = bz - radius; z <= bz + radius; z++) {
					double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)));
					if (distance < radius * radius) {
						mutableDisk.set(x, level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1, z);
						boolean fade = !mutableDisk.closerThan(surfacePos, radius * 0.8D);
						if (level.getBlockState(mutableDisk).is(config.replaceableBlocks())) {
							generated = true;
							if (fade) {
								if (random.nextFloat() > 0.65F) {
									level.setBlock(mutableDisk, disk, Block.UPDATE_CLIENTS);
								}
							} else {
								level.setBlock(mutableDisk, disk, Block.UPDATE_CLIENTS);
							}
						}
					}
				}
			}
		}
		// COLUMN
		BlockState columnState = config.state();
		BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
		int pillarHeight = config.height().sample(random);
		generated = placeAtPos(level, blockPos, mutablePos, columnState, pillarHeight) || generated;

		int maxSurroundingPillarHeight = pillarHeight - 1;
		if (maxSurroundingPillarHeight > 0) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() < config.surroundingPillarChance()) {
					generated = placeAtPos(level, blockPos.relative(direction), mutablePos, columnState, UniformInt.of(1, maxSurroundingPillarHeight).sample(random)) || generated;
				}
			}
		}
		return generated;
	}

	private boolean placeAtPos(
		@NotNull WorldGenLevel level,
		@NotNull BlockPos startPos,
		BlockPos.@NotNull MutableBlockPos mutablePos,
		BlockState columnState,
		int height
	) {
		boolean generated = false;
		mutablePos.set(startPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, startPos.getX(), startPos.getZ()) - 1));
		if (level.getBlockState(mutablePos).getFluidState().isEmpty()) {
			for (int i = 0; i < height; i++) {
				BlockState state = level.getBlockState(mutablePos.move(Direction.UP));
				if (state.canBeReplaced()) {
					level.setBlock(mutablePos, columnState, Block.UPDATE_CLIENTS);
					generated = true;
				}
			}
		}
		return generated;
	}

}
