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
import net.frozenblock.lib.worldgen.feature.api.feature.config.ComboFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ComboFeature extends Feature<ComboFeatureConfig> {

	public ComboFeature(Codec<ComboFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<ComboFeatureConfig> context) {
		final WorldGenLevel level = context.level();
		final ComboFeatureConfig config = context.config();
		final RandomSource random = context.random();
		final BlockPos pos = context.origin();
		final ChunkGenerator chunkGenerator = context.chunkGenerator();

		boolean generated = false;
		for (Holder<PlacedFeature> feature : config.features()) {
			if (this.place(level, feature, chunkGenerator, random, pos)) generated = true;
		}
		return generated;
	}

	public boolean place(WorldGenLevel level, Holder<PlacedFeature> placedFeature, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
		return placedFeature.value().place(level, chunkGenerator, random, pos);
	}

}
