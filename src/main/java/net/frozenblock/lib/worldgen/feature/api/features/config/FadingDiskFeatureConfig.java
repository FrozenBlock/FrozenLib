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
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record FadingDiskFeatureConfig(boolean useHeightmapInsteadOfCircularPlacement, BlockStateProvider innerState, BlockStateProvider outerState, IntProvider radius, float placementChance, float innerChance, float innerPercent, float fadeStartDistancePercent, HolderSet<Block> innerReplaceableBlocks, HolderSet<Block> outerReplaceableBlocks, Heightmap.Types heightmap) implements FeatureConfiguration {
	public static final Codec<FadingDiskFeatureConfig> CODEC = RecordCodecBuilder.create(
		(instance) -> instance.group(
			Codec.BOOL.fieldOf("use_heightmap_instead_of_circular_placement").forGetter(config -> config.useHeightmapInsteadOfCircularPlacement),
			BlockStateProvider.CODEC.fieldOf("inner_state").forGetter(config -> config.innerState),
			BlockStateProvider.CODEC.fieldOf("outer_state").forGetter(config -> config.outerState),
			IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
			Codec.FLOAT.fieldOf("placement_chance").forGetter(config -> config.placementChance),
			Codec.FLOAT.fieldOf("inner_chance").forGetter(config -> config.innerChance),
			Codec.FLOAT.fieldOf("inner_percent").forGetter(config -> config.innerPercent),
			Codec.FLOAT.fieldOf("fade_start_distance_percent").forGetter(config -> config.fadeStartDistancePercent),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("inner_replaceable_blocks").forGetter((config) -> config.innerReplaceableBlocks),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("outer_replaceable_blocks").forGetter((config) -> config.outerReplaceableBlocks),
			Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((config) -> config.heightmap)
		).apply(instance, FadingDiskFeatureConfig::new)
	);
}
