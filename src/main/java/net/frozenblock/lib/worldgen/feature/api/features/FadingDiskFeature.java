/*
 * Copyright (C) 2024 FrozenBlock
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
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.NotNull;

public class FadingDiskFeature extends Feature<FadingDiskFeatureConfig> {

	public FadingDiskFeature(Codec<FadingDiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<FadingDiskFeatureConfig> context) {
		AtomicBoolean success = new AtomicBoolean();
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		FadingDiskFeatureConfig config = context.config();
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
					double distance = Math.pow((double) bx - x, 2) + Math.pow((double) bz - z, 2);
					success.set(placeAtPos(level, config, s, random, radius, mutableDisk, x, level.getHeight(heightmap, x, z) - 1, z, distance, true));
				} else {
					int maxY = by + radius;
					for (int y = by - radius; y <= maxY; y++) {
						double distance = Math.pow((double) bx - x, 2) + Math.pow((double) by - y, 2) + Math.pow((double) bz - z, 2);
						success.set(placeAtPos(level, config, s, random, radius, mutableDisk, x, y, z, distance, false));
					}
				}
			}
		}

		return success.get();
	}

	private static boolean placeAtPos(WorldGenLevel level, FadingDiskFeatureConfig config, BlockPos s, RandomSource random, int radius, BlockPos.MutableBlockPos mutableDisk, int x, int y, int z, double distance, boolean useHeightMapAndNotCircular) {
		if (distance < Math.pow(radius, 2)) {
			mutableDisk.set(x, y, z);
			BlockState state = level.getBlockState(mutableDisk);
			if (!useHeightMapAndNotCircular && isBlockExposed(level, mutableDisk)) {
				boolean inner = mutableDisk.closerThan(s, radius * config.innerChance());
				boolean fade = !inner && !mutableDisk.closerThan(s, radius * config.fadeStartDistancePercent());
				if (random.nextFloat() < config.placementChance()) {
					if (fade) {
						if (random.nextFloat() > 0.5F && state.is(config.outerReplaceableBlocks())) {
							level.setBlock(mutableDisk, config.outerState().getState(random, mutableDisk), 3);
							return true;
						}
					} else {
						boolean choseInner = inner && random.nextFloat() < config.innerChance();
						if (state.is(choseInner ? config.innerReplaceableBlocks() : config.outerReplaceableBlocks())) {
							BlockStateProvider newState = choseInner ? config.innerState() : config.outerState();
							level.setBlock(mutableDisk, newState.getState(random, mutableDisk), 3);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isBlockExposed(WorldGenLevel level, @NotNull BlockPos blockPos) {
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		for (Direction direction : Direction.values()) {
			mutableBlockPos.move(direction);
			BlockState blockState = level.getBlockState(mutableBlockPos);
			if (blockState.isAir() || blockState.is(BlockTags.FIRE)) {
				return true;
			}
			mutableBlockPos.move(direction, -1);
		}
		return false;
	}

}
