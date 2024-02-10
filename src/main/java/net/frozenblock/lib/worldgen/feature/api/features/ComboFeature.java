/*
 * Copyright 2023-2024 FrozenBlock
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
import net.frozenblock.lib.worldgen.feature.api.features.config.ComboFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public class ComboFeature extends Feature<ComboFeatureConfig> {

    public ComboFeature(Codec<ComboFeatureConfig> codec) {
        super(codec);
    }

	@Override
    public boolean place(@NotNull FeaturePlaceContext<ComboFeatureConfig> context) {
		WorldGenLevel worldGenLevel = context.level();
		ComboFeatureConfig config = context.config();
		RandomSource randomSource = context.random();
		BlockPos blockPos = context.origin();
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		boolean placedAny = false;
		for (Holder<PlacedFeature> feature : config.features()) {
			if (this.place(worldGenLevel, feature, chunkGenerator, randomSource, blockPos))
				placedAny = true;
		}
		return placedAny;
    }

	public boolean place(WorldGenLevel worldGenLevel, @NotNull Holder<PlacedFeature> holder, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos) {
		return holder.value().place(worldGenLevel, chunkGenerator, randomSource, blockPos);
	}

}
