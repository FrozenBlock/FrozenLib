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

package net.frozenblock.lib.worldgen.feature.api.feature.disk;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.worldgen.feature.api.feature.disk.config.BallBlockPlacement;
import net.frozenblock.lib.worldgen.feature.api.feature.disk.config.BallFeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class BallFeature extends Feature<BallFeatureConfig> {

	public BallFeature(Codec<BallFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(@NotNull FeaturePlaceContext<BallFeatureConfig> context) {
		boolean generated = false;
		BallFeatureConfig config = context.config();
		BlockPos blockPos = context.origin();
		WorldGenLevel level = context.level();
		RandomSource random = level.getRandom();

		int radius = config.placementRadius().sample(random);
		BallBlockPlacement blockPlacement = config.ballBlockPlacement();
		Heightmap.Types heightmapType = config.heightmapType().orElse(null);
		boolean missingHeightmap = heightmapType == null;

		BlockPos.MutableBlockPos mutable = blockPos.mutable();
		int startX = blockPos.getX();
		int startY = blockPos.getY();
		int startZ = blockPos.getZ();

		for (int x = startX - radius; x <= startX + radius; x++) {
			for (int z = startZ - radius; z <= startZ + radius; z++) {
				if (!missingHeightmap) {
					mutable.set(x, level.getHeight(heightmapType, x, z) - 1, z);
					generated = blockPlacement.generate(level, blockPos, mutable, true, radius, random) || generated;
				} else {
					for (int y = startY - radius; y <= startY + radius; y++) {
						mutable.set(x, y, z);
						generated = blockPlacement.generate(level, blockPos, mutable, false, radius, random) || generated;
					}
				}
			}
		}
		return generated;
	}

}
