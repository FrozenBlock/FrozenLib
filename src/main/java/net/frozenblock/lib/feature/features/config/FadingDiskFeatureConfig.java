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
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class FadingDiskFeatureConfig implements FeatureConfiguration {
    public static final Codec<FadingDiskFeatureConfig> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(
					BlockStateProvider.CODEC.fieldOf("innerState").forGetter(config -> config.innerState),
					BlockStateProvider.CODEC.fieldOf("outerState").forGetter(config -> config.outerState),
					IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius),
					RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("replaceable").forGetter((config) -> config.replaceable)
			).apply(instance, FadingDiskFeatureConfig::new)
    );

    public final BlockStateProvider innerState;
    public final BlockStateProvider outerState;
    public final IntProvider radius;
	public final HolderSet<Block> replaceable;

    public FadingDiskFeatureConfig(BlockStateProvider innerState, BlockStateProvider outerState, IntProvider radius, HolderSet<Block> replaceable) {
		this.innerState = innerState;
		this.outerState = outerState;
		this.radius = radius;
		this.replaceable = replaceable;
    }
}
