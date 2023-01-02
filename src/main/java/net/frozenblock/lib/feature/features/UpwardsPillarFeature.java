/*
 * Copyright 2022-2023 FrozenBlock
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
import net.frozenblock.lib.feature.features.config.PillarFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;

public class UpwardsPillarFeature extends Feature<PillarFeatureConfig> {
    public UpwardsPillarFeature(Codec<PillarFeatureConfig> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<PillarFeatureConfig> context) {
        boolean bl = false;
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = level.getRandom();
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        int bx = blockPos.getX();
        int bz = blockPos.getZ();
        int by = blockPos.getY();
        int height = context.config().height.sample(random);
        for (int y = 0; y < height; y++) {
            if (context.config().replaceable.contains(level.getBlockState(mutable).getBlockHolder()) || level.getBlockState(mutable).isAir() || level.getBlockState(mutable).getFluidState() != Fluids.EMPTY.defaultFluidState()) {
                bl = true;
                level.setBlock(mutable, context.config().columnBlock, 3);
                mutable.set(bx, by + y, bz);
            } else {
                mutable.set(bx, by + y, bz);
            }
        }
        return bl;
    }

}
