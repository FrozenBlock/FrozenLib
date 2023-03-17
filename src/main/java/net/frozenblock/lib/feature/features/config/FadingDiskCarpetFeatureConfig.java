/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.feature.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class FadingDiskCarpetFeatureConfig implements FeatureConfiguration {
    public static final Codec<FadingDiskCarpetFeatureConfig> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
					Codec.BOOL.fieldOf("useHeightMapAndNotCircular").forGetter(config -> config.useHeightMapAndNotCircular),
					BlockStateProvider.CODEC.fieldOf("innerState").forGetter(config -> config.innerState),
					BlockStateProvider.CODEC.fieldOf("outerState").forGetter(config -> config.outerState),
					IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
					Codec.FLOAT.fieldOf("placeChance").forGetter(config -> config.placeChance),
					Codec.FLOAT.fieldOf("innerChance").forGetter(config -> config.innerChance),
					Codec.FLOAT.fieldOf("innerPercent").forGetter(config -> config.innerPercent),
					Codec.FLOAT.fieldOf("startFadePercent").forGetter(config -> config.startFadePercent),
					TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("placeableOn").forGetter((config) -> config.placeableOn),
					TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("innerReplaceable").forGetter((config) -> config.innerReplaceable),
					TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("outerReplaceable").forGetter((config) -> config.outerReplaceable)
			).apply(instance, FadingDiskCarpetFeatureConfig::new)
	);

	public final boolean useHeightMapAndNotCircular;
    public final BlockStateProvider innerState;
    public final BlockStateProvider outerState;
    public final IntProvider radius;
	public final float placeChance;
	public final float innerChance;
	public final float innerPercent;
	public final float startFadePercent;
	public final TagKey<Block> placeableOn;
	public final TagKey<Block> innerReplaceable;
	public final TagKey<Block> outerReplaceable;

    public FadingDiskCarpetFeatureConfig(boolean useHeightMapAndNotCircular, BlockStateProvider innerState, BlockStateProvider outerState, IntProvider radius, float placeChance, float innerChance, float innerPercent, float startFadePercent, TagKey<Block> placeableOn, TagKey<Block> innerReplaceable, TagKey<Block> outerReplaceable) {
		this.useHeightMapAndNotCircular = useHeightMapAndNotCircular;
		this.innerState = innerState;
		this.outerState = outerState;
		this.radius = radius;
		this.placeChance = placeChance;
		this.innerChance = innerChance;
		this.innerPercent = innerPercent;
		this.startFadePercent = startFadePercent;
		this.placeableOn = placeableOn;
		this.innerReplaceable = innerReplaceable;
		this.outerReplaceable = outerReplaceable;
    }
}
