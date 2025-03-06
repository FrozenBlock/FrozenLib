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

package net.frozenblock.lib.worldgen.feature.api.feature.noise_path.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record NoisePathFeatureConfig(
	NoiseBandPlacement noiseBandPlacement,
	int placementRadius
) implements FeatureConfiguration {
	public static final Codec<NoisePathFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		NoiseBandPlacement.CODEC.fieldOf("noise_band_placement").forGetter(config -> config.noiseBandPlacement),
		Codec.intRange(1, 16).fieldOf("placement_radius").orElse(10).forGetter(config -> config.placementRadius)
	).apply(instance, NoisePathFeatureConfig::new));
}
