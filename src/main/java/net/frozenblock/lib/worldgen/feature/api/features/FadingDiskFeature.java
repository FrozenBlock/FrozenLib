/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
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
		Consumer<LevelAccessor> consumer = levelAccessor -> {
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
		};

		if (radius < 15) {
			consumer.accept(level);
		} else {
			ServerLevel serverLevel = level.getLevel();
			serverLevel.getServer().executeBlocking(() -> consumer.accept(serverLevel));
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
