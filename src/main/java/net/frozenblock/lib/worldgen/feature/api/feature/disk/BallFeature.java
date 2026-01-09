/*
 * Copyright (C) 2024-2026 FrozenBlock
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

public class BallFeature extends Feature<BallFeatureConfig> {

	public BallFeature(Codec<BallFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<BallFeatureConfig> context) {
		final BallFeatureConfig config = context.config();
		final BlockPos pos = context.origin();
		final WorldGenLevel level = context.level();
		final RandomSource random = level.getRandom();

		final int radius = config.placementRadius().sample(random);
		final BallBlockPlacement blockPlacement = config.ballBlockPlacement();
		final Heightmap.Types heightmapType = config.heightmapType().orElse(null);
		final boolean missingHeightmap = heightmapType == null;

		final BlockPos.MutableBlockPos mutable = pos.mutable();
		final int startX = pos.getX();
		final int startY = pos.getY();
		final int startZ = pos.getZ();

		boolean generated = false;
		for (int x = startX - radius; x <= startX + radius; x++) {
			for (int z = startZ - radius; z <= startZ + radius; z++) {
				if (!missingHeightmap) {
					mutable.set(x, level.getHeight(heightmapType, x, z) - 1, z);
					generated = blockPlacement.generate(level, pos, mutable, true, radius, random) || generated;
				} else {
					for (int y = startY - radius; y <= startY + radius; y++) {
						mutable.set(x, y, z);
						generated = blockPlacement.generate(level, pos, mutable, false, radius, random) || generated;
					}
				}
			}
		}
		return generated;
	}

}
