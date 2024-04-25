/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record FadingDiskCarpetFeatureConfig(boolean useHeightmapInsteadOfCircularPlacement, BlockStateProvider innerState, BlockStateProvider outerState, IntProvider radius, float placementChance, float innerPercent, float innerChance, float fadeStartDistancePercent, Heightmap.Types heightmap) implements FeatureConfiguration {
	public static final Codec<FadingDiskCarpetFeatureConfig> CODEC = RecordCodecBuilder.create(
		(instance) -> instance.group(
			Codec.BOOL.fieldOf("use_heightmap_instead_of_circular_placement").forGetter(config -> config.useHeightmapInsteadOfCircularPlacement),
			BlockStateProvider.CODEC.fieldOf("inner_state").forGetter(config -> config.innerState),
			BlockStateProvider.CODEC.fieldOf("outer_state").forGetter(config -> config.outerState),
			IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
			Codec.FLOAT.fieldOf("placement_chance").forGetter(config -> config.placementChance),
			Codec.FLOAT.fieldOf("inner_chance").forGetter(config -> config.innerChance),
			Codec.FLOAT.fieldOf("inner_percent").forGetter(config -> config.innerPercent),
			Codec.FLOAT.fieldOf("fade_start_distance_percent").forGetter(config -> config.fadeStartDistancePercent),
			Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((config) -> config.heightmap)
		).apply(instance, FadingDiskCarpetFeatureConfig::new)
	);
}
