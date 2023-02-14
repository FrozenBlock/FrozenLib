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


package net.frozenblock.lib.feature.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.feature.features.config.FadingDiskFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class FadingDiskFeature extends Feature<FadingDiskFeatureConfig> {
    public FadingDiskFeature(Codec<FadingDiskFeatureConfig> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<FadingDiskFeatureConfig> context) {
        boolean bl = false;
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
		FadingDiskFeatureConfig config = context.config();
        BlockPos s = blockPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()));
        RandomSource random = level.getRandom();
        int radius = config.radius.sample(random);
        //DISK
        BlockPos.MutableBlockPos mutableDisk = s.mutable();
        int bx = s.getX();
        int bz = s.getZ();
		int y = s.getY();
        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int z = bz - radius; z <= bz + radius; z++) {
                double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z));
                if (distance < radius * radius) {
                    mutableDisk.set(x, y, z);
					boolean inner = !mutableDisk.closerThan(s, radius * 0.475);
                    boolean fade = !mutableDisk.closerThan(s, radius * 0.8);
					mutableDisk.set(blockPos.atY(level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z)).below());
					if (config.replaceable.contains(level.getBlockState(mutableDisk).getBlock().builtInRegistryHolder())) {
						if (fade) {
							if (random.nextFloat() > 0.5F) {
								level.setBlock(mutableDisk, config.outerState.getState(random, mutableDisk), 3);
								bl = true;
							}
						} else {
							level.setBlock(mutableDisk, !inner ? config.outerState.getState(random, mutableDisk) : config.innerState.getState(random, mutableDisk), 3);
							bl = true;
						}
                    }
                }
            }
        }
        return bl;
    }

}
