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

package net.frozenblock.lib.worldgen.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record AirOrWaterInAreaPathFeatureConfig(
	BlockStateProvider state,
	int radius,
	EasyNoiseSampler.NoiseType noiseType,
	double noiseScale,
	double minThreshold,
	double maxThreshold,
	boolean useY,
	boolean scaleY,
	boolean is3D,
	int airOrWaterSearchDistance,
	HolderSet<Block> replaceableBlocks,
	float placementChance
) implements FeatureConfiguration {
	public static final Codec<AirOrWaterInAreaPathFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		BlockStateProvider.CODEC.fieldOf("state").forGetter((config) -> config.state),
		Codec.intRange(1, 16).fieldOf("radius").orElse(10).forGetter((config) -> config.radius),
		EasyNoiseSampler.NoiseType.CODEC.fieldOf("noise_type").forGetter((config) -> config.noiseType),
		Codec.doubleRange(0.0001D, 128D).fieldOf("noise_scale").orElse(0.05).forGetter((config) -> config.noiseScale),
		Codec.doubleRange(-1D, 1D).fieldOf("min_threshold").orElse(0.2).forGetter((config) -> config.minThreshold),
		Codec.doubleRange(-1D, 1D).fieldOf("max_threshold").orElse(1D).forGetter((config) -> config.maxThreshold),
		Codec.BOOL.fieldOf("use_y").orElse(false).forGetter((config) -> config.useY),
		Codec.BOOL.fieldOf("scale_y").orElse(false).forGetter((config) -> config.scaleY),
		Codec.BOOL.fieldOf("is_3d").orElse(false).forGetter((config) -> config.is3D),
		Codec.intRange(1, 8).fieldOf("air_or_water_search_distance").orElse(2).forGetter((config) -> config.airOrWaterSearchDistance),
		RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter((config) -> config.replaceableBlocks),
		Codec.floatRange(0, 1).fieldOf("placement_chance").orElse(1F).forGetter((config) -> config.placementChance)
	).apply(instance, AirOrWaterInAreaPathFeatureConfig::new));
}
