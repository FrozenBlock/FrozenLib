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

package net.frozenblock.lib.levelgen.feature.api.feature.disk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.levelgen.feature.api.feature.disk.config.BallBlockPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record BallFeature(
	BallBlockPlacement ballBlockPlacement,
	Optional<Heightmap.Types> heightmapType,
	IntProvider placementRadius
) implements Feature {
	public static final MapCodec<BallFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BallBlockPlacement.CODEC.fieldOf("block_placement").forGetter(BallFeature::ballBlockPlacement),
		Heightmap.Types.CODEC.lenientOptionalFieldOf("heightmap").forGetter(BallFeature::heightmapType),
		IntProviders.codec(1, 16).fieldOf("placement_radius").forGetter(BallFeature::placementRadius)
	).apply(instance, BallFeature::new));

	@Override
	public MapCodec<? extends Feature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		final int radius = this.placementRadius().sample(random);
		final Heightmap.Types heightmapType = this.heightmapType.orElse(null);
		final boolean missingHeightmap = heightmapType == null;

		final BlockPos.MutableBlockPos mutable = origin.mutable();
		final int startX = origin.getX();
		final int startY = origin.getY();
		final int startZ = origin.getZ();

		boolean generated = false;
		for (int x = startX - radius; x <= startX + radius; x++) {
			for (int z = startZ - radius; z <= startZ + radius; z++) {
				if (!missingHeightmap) {
					mutable.set(x, level.getHeight(heightmapType, x, z) - 1, z);
					generated = this.ballBlockPlacement.generate(level, origin, mutable, true, radius, random) || generated;
				} else {
					for (int y = startY - radius; y <= startY + radius; y++) {
						mutable.set(x, y, z);
						generated = this.ballBlockPlacement.generate(level, origin, mutable, false, radius, random) || generated;
					}
				}
			}
		}
		return generated;
	}
}
