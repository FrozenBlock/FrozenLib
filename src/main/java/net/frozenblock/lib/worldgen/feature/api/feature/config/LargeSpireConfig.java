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

package net.frozenblock.lib.worldgen.feature.api.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class LargeSpireConfig implements FeatureConfiguration {
	public static final Codec<LargeSpireConfig> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter(config -> config.floorToCeilingSearchRange),
			IntProvider.codec(1, 60).fieldOf("column_radius").forGetter(config -> config.columnRadius),
			BlockStateProvider.CODEC.fieldOf("block").forGetter(config-> config.pathBlock),
			FloatProvider.codec(0F, 20F).fieldOf("height_scale").forGetter(config -> config.heightScale),
			Codec.floatRange(0.1F, 1F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter(config -> config.maxColumnRadiusToCaveHeightRatio),
			FloatProvider.codec(0.1F, 10F).fieldOf("stalactite_bluntness").forGetter(config -> config.stalactiteBluntness),
			FloatProvider.codec(0.1F, 10F).fieldOf("stalagmite_bluntness").forGetter(config -> config.stalagmiteBluntness),
			FloatProvider.codec(0F, 2F).fieldOf("wind_speed").forGetter(config -> config.windSpeed),
			Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(config -> config.minRadiusForWind),
			Codec.floatRange(0F, 5F).fieldOf("min_bluntness_for_wind").forGetter(config -> config.minBluntnessForWind),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("base_blocks").forGetter(config -> config.baseBlocks),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter(config -> config.replaceable)
		).apply(instance, LargeSpireConfig::new)
	);

	public final int floorToCeilingSearchRange;
	public final IntProvider columnRadius;
	public final BlockStateProvider pathBlock;
	public final FloatProvider heightScale;
	public final float maxColumnRadiusToCaveHeightRatio;
	public final FloatProvider stalactiteBluntness;
	public final FloatProvider stalagmiteBluntness;
	public final FloatProvider windSpeed;
	public final int minRadiusForWind;
	public final float minBluntnessForWind;
	public final HolderSet<Block> baseBlocks;
	public final HolderSet<Block> replaceable;

	public LargeSpireConfig(
		int floorToCeilingSearchRange,
		IntProvider columnRadius,
		BlockStateProvider pathBlock,
		FloatProvider heightScale,
		float maxColumnRadiusToCaveHeightRatio,
		FloatProvider stalactiteBluntness,
		FloatProvider stalagmiteBluntness,
		FloatProvider windSpeed,
		int minRadiusForWind,
		float minBluntnessForWind,
		HolderSet<Block> baseBlocks,
		HolderSet<Block> replaceable
	) {
		this.floorToCeilingSearchRange = floorToCeilingSearchRange;
		this.columnRadius = columnRadius;
		this.pathBlock = pathBlock;
		this.heightScale = heightScale;
		this.maxColumnRadiusToCaveHeightRatio = maxColumnRadiusToCaveHeightRatio;
		this.stalactiteBluntness = stalactiteBluntness;
		this.stalagmiteBluntness = stalagmiteBluntness;
		this.windSpeed = windSpeed;
		this.minRadiusForWind = minRadiusForWind;
		this.minBluntnessForWind = minBluntnessForWind;
		this.baseBlocks = baseBlocks;
		this.replaceable = replaceable;
	}
}

