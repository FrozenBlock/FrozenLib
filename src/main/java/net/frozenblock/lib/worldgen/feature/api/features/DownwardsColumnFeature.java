/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.features;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.worldgen.feature.api.features.config.ColumnFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class DownwardsColumnFeature extends Feature<ColumnFeatureConfig> {

    public DownwardsColumnFeature(Codec<ColumnFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<ColumnFeatureConfig> context) {
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
            if (context.config().replaceableBlocks().contains(level.getBlockState(mutable).getBlockHolder()) || level.getBlockState(mutable).isAir() || level.getBlockState(mutable).getFluidState() != Fluids.EMPTY.defaultFluidState()) {
                bl = true;
                level.setBlock(mutable, context.config().state(), 3);
                mutable.set(bx, by + y, bz);
            } else {
                mutable.set(bx, by + y, bz);
            }
        }
        return bl;
    }

}
