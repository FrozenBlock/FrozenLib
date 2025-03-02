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

package net.frozenblock.lib.worldgen.feature.api.feature.disk.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BallFeatureConfig(BallBlockPlacement ballBlockPlacement, Optional<Heightmap.Types> heightmapType, IntProvider placementRadius) implements FeatureConfiguration {
	public static final Codec<BallFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		BallBlockPlacement.CODEC
			.fieldOf("block_placement")
			.forGetter(config -> config.ballBlockPlacement),
		Heightmap.Types.CODEC
			.lenientOptionalFieldOf("heightmap")
			.forGetter(config -> config.heightmapType),
		IntProvider.codec(1, 16).fieldOf("placement_radius").forGetter(config -> config.placementRadius)
	).apply(instance, BallFeatureConfig::new));
}
