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

package net.frozenblock.lib.worldgen.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PathSwapUnderWaterTagFeatureConfig(BlockStateProvider state, BlockStateProvider underWaterState, int radius, int noise, double noiseScale, double minThreshold, double maxThreshold, boolean useY, boolean scaleY, boolean is3D, boolean onlyPlaceWhenExposed, TagKey<Block> replaceableBlocks, float placementChance) implements FeatureConfiguration {
	public static final Codec<PathSwapUnderWaterTagFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		BlockStateProvider.CODEC.fieldOf("state").forGetter((config) -> config.state),
		BlockStateProvider.CODEC.fieldOf("under_water_state").forGetter((config) -> config.underWaterState),
		Codec.intRange(1, 64).fieldOf("radius").orElse(10).forGetter((config) -> config.radius),
		Codec.intRange(1, 4).fieldOf("noise").orElse(4).forGetter((config) -> config.noise),
		Codec.doubleRange(0.0001, 128).fieldOf("noise_scale").orElse(0.05).forGetter((config) -> config.noiseScale),
		Codec.doubleRange(-1, 1).fieldOf("min_threshold").orElse(0.2).forGetter((config) -> config.minThreshold),
		Codec.doubleRange(-1, 1).fieldOf("max_threshold").orElse(1D).forGetter((config) -> config.maxThreshold),
		Codec.BOOL.fieldOf("use_y").orElse(false).forGetter((config) -> config.useY),
		Codec.BOOL.fieldOf("scale_y").orElse(false).forGetter((config) -> config.scaleY),
		Codec.BOOL.fieldOf("is_3d").orElse(false).forGetter((config) -> config.is3D),
		Codec.BOOL.fieldOf("only_place_when_exposed").orElse(false).forGetter((config) -> config.onlyPlaceWhenExposed),
		TagKey.codec(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter((config) -> config.replaceableBlocks),
		Codec.floatRange(0, 1).fieldOf("placement_chance").orElse(1F).forGetter((config) -> config.placementChance)
	).apply(instance, PathSwapUnderWaterTagFeatureConfig::new));
}
