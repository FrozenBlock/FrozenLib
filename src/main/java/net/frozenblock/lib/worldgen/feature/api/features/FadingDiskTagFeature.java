/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.frozenblock.lib.worldgen.feature.api.features.config.FadingDiskTagFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class FadingDiskTagFeature extends Feature<FadingDiskTagFeatureConfig> {

    public FadingDiskTagFeature(Codec<FadingDiskTagFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<FadingDiskTagFeatureConfig> context) {
		final AtomicBoolean[] bl = {new AtomicBoolean(false)};
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
		FadingDiskTagFeatureConfig config = context.config();
		boolean useHeightMapAndNotCircular = config.useHeightMapAndNotCircular();
		Heightmap.Types heightmap = config.heightmap();
        BlockPos s = useHeightMapAndNotCircular ? blockPos.atY(level.getHeight(heightmap, blockPos.getX(), blockPos.getZ())) : blockPos;
        RandomSource random = level.getRandom();
        int radius = config.radius().sample(random);
        //DISK
        BlockPos.MutableBlockPos mutableDisk = s.mutable();
        int bx = s.getX();
		int by = s.getY();
        int bz = s.getZ();

		Consumer<LevelAccessor> consumer = (levelAccessor) -> {
			for (int x = bx - radius; x <= bx + radius; x++) {
				for (int z = bz - radius; z <= bz + radius; z++) {
					if (useHeightMapAndNotCircular) {
						double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z));
						if (distance < radius * radius) {
							mutableDisk.set(x, level.getHeight(heightmap, x, z) - 1, z);
							BlockState state = level.getBlockState(mutableDisk);
							boolean inner = mutableDisk.closerThan(s, radius * config.innerPercent());
							boolean fade = !inner && !mutableDisk.closerThan(s, radius * config.startFadePercent());
							boolean choseInner;
							if (random.nextFloat() < config.placeChance()) {
								if (fade) {
									if (random.nextFloat() > 0.5F && state.is(config.outerReplaceable())) {
										level.setBlock(mutableDisk, config.outerState().getState(random, mutableDisk), 3);
										bl[0].set(true);
									}
								} else if (state.is((choseInner = (inner && random.nextFloat() < config.innerChance())) ? config.innerReplaceable() : config.outerReplaceable())) {
									level.setBlock(mutableDisk, choseInner ? config.innerState().getState(random, mutableDisk) : config.outerState().getState(random, mutableDisk), 3);
									bl[0].set(true);
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
									boolean fade = !inner && !mutableDisk.closerThan(s, radius * config.startFadePercent());
									boolean choseInner;
									if (random.nextFloat() < config.placeChance()) {
										if (fade) {
											if (random.nextFloat() > 0.5F && state.is(config.outerReplaceable())) {
												level.setBlock(mutableDisk, config.outerState().getState(random, mutableDisk), 3);
												bl[0].set(true);
											}
										} else if (state.is((choseInner = (inner && random.nextFloat() < config.innerChance())) ? config.innerReplaceable() : config.outerReplaceable())) {
											level.setBlock(mutableDisk, choseInner ? config.innerState().getState(random, mutableDisk) : config.outerState().getState(random, mutableDisk), 3);
											bl[0].set(true);
										}
									}
								}
							}
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

		return true;
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

}
