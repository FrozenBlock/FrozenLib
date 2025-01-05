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
import java.util.concurrent.atomic.AtomicBoolean;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskTagFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class FadingDiskWithPileTagFeature extends Feature<FadingDiskTagFeatureConfig> {

	public FadingDiskWithPileTagFeature(Codec<FadingDiskTagFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<FadingDiskTagFeatureConfig> context) {
		final AtomicBoolean bl = new AtomicBoolean(false);
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		FadingDiskTagFeatureConfig config = context.config();
		boolean useHeightMapAndNotCircular = config.useHeightmapInsteadOfCircularPlacement();
		Heightmap.Types heightmap = config.heightmap();
		BlockPos s = useHeightMapAndNotCircular ? blockPos.atY(level.getHeight(heightmap, blockPos.getX(), blockPos.getZ())) : blockPos;
		RandomSource random = level.getRandom();
		int radius = config.radius().sample(random);
		//DISK
		BlockPos.MutableBlockPos mutableDisk = s.mutable();
		int bx = s.getX();
		int by = s.getY();
		int bz = s.getZ();

		for (int x = bx - radius; x <= bx + radius; x++) {
			for (int z = bz - radius; z <= bz + radius; z++) {
				if (useHeightMapAndNotCircular) {
					double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z));
					if (distance < radius * radius) {
						mutableDisk.set(x, level.getHeight(heightmap, x, z) - 1, z);
						BlockState state = level.getBlockState(mutableDisk);
						boolean inner = mutableDisk.closerThan(s, radius * config.innerPercent());
						boolean fade = !inner && !mutableDisk.closerThan(s, radius * config.fadeStartDistancePercent());
						boolean choseInner;
						if (random.nextFloat() < config.placementChance()) {
							if (fade) {
								if (random.nextFloat() > 0.5F && state.is(config.outerReplaceableBlocks())) {
									level.setBlock(mutableDisk, config.outerState().getState(random, mutableDisk), 3);
									bl.set(true);
								}
							} else if (state.is((choseInner = (inner && random.nextFloat() < config.innerChance())) ? config.innerReplaceableBlocks() : config.outerReplaceableBlocks())) {
								level.setBlock(mutableDisk, choseInner ? config.innerState().getState(random, mutableDisk) : config.outerState().getState(random, mutableDisk), 3);
								bl.set(true);
							}
						}
					}
				} else {
					for (int y = by - radius; y <= by + radius; y++) {
						double distance = ((bx - x) * (bx - x) + (by - y) * (by - y) + (bz - z) * (bz - z));
						if (distance < radius * radius) {
							mutableDisk.set(x, y, z);
							BlockState state = level.getBlockState(mutableDisk);
							if (isBlockExposedToAir(level, mutableDisk)) {
								boolean inner = mutableDisk.closerThan(s, radius * config.innerPercent());
								boolean fade = !inner && !mutableDisk.closerThan(s, radius * config.fadeStartDistancePercent());
								boolean choseInner;
								if (random.nextFloat() < config.placementChance()) {
									if (fade) {
										if (random.nextFloat() > 0.5F && state.is(config.outerReplaceableBlocks())) {
											level.setBlock(mutableDisk, config.outerState().getState(random, mutableDisk), 3);
											bl.set(true);
										}
									} else if (state.is((choseInner = (inner && random.nextFloat() < config.innerChance())) ? config.innerReplaceableBlocks() : config.outerReplaceableBlocks())) {
										level.setBlock(mutableDisk, choseInner ? config.innerState().getState(random, mutableDisk) : config.outerState().getState(random, mutableDisk), 3);
										bl.set(true);
									}
								}
							}
						}
					}
				}
			}
		}

		bl.set(this.placePile(context) || bl.get());

		return bl.get();
	}

	public static boolean isBlockExposedToAir(WorldGenLevel level, @NotNull BlockPos blockPos) {
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		for (Direction direction : Direction.values()) {
			mutableBlockPos.move(direction);
			if (level.getBlockState(mutableBlockPos).isAir()) {
				return true;
			}
			mutableBlockPos.move(direction, -1);
		}
		return false;
	}

	public boolean placePile(@NotNull FeaturePlaceContext<FadingDiskTagFeatureConfig> context) {
		BlockPos blockPos = context.origin();
		WorldGenLevel worldGenLevel = context.level();
		RandomSource randomSource = context.random();
		FadingDiskTagFeatureConfig config = context.config();
		if (blockPos.getY() < worldGenLevel.getMinY() + 5) {
			return false;
		} else {
			int i = 2 + randomSource.nextInt(2);
			int j = 2 + randomSource.nextInt(2);

			for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-i, 0, -j), blockPos.offset(i, 1, j))) {
				int k = blockPos.getX() - blockPos2.getX();
				int l = blockPos.getZ() - blockPos2.getZ();
				if ((float) (k * k + l * l) <= randomSource.nextFloat() * 10.0F - randomSource.nextFloat() * 6.0F) {
					this.tryPlaceBlock(worldGenLevel, blockPos2, randomSource, config);
				} else if ((double) randomSource.nextFloat() < 0.031D) {
					this.tryPlaceBlock(worldGenLevel, blockPos2, randomSource, config);
				}
			}

			return true;
		}
	}

	private boolean mayPlaceOn(@NotNull LevelAccessor level, @NotNull BlockPos pos, RandomSource random) {
		BlockPos blockPos = pos.below();
		BlockState blockState = level.getBlockState(blockPos);
		return blockState.is(Blocks.DIRT_PATH) ? random.nextBoolean() : blockState.isFaceSturdy(level, blockPos, Direction.UP);
	}

	private void tryPlaceBlock(@NotNull LevelAccessor level, BlockPos pos, RandomSource random, FadingDiskTagFeatureConfig config) {
		if (level.isEmptyBlock(pos) && this.mayPlaceOn(level, pos, random)) {
			level.setBlock(pos, config.innerState().getState(random, pos), 4);
		}
	}

}
