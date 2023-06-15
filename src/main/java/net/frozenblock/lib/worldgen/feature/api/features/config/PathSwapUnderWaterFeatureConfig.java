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

package net.frozenblock.lib.worldgen.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PathSwapUnderWaterFeatureConfig(BlockStateProvider pathBlock, BlockStateProvider waterPathBlock, int radius, int noise, double multiplier, double minThresh, double maxThresh, boolean useY, boolean multiplyY, boolean is3D, boolean onlyExposed, HolderSet<Block> replaceable, float chance) implements FeatureConfiguration {
	public static final Codec<PathSwapUnderWaterFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		BlockStateProvider.CODEC.fieldOf("block").forGetter((config) -> config.pathBlock),
		BlockStateProvider.CODEC.fieldOf("waterPathBlock").forGetter((config) -> config.waterPathBlock),
		Codec.intRange(1, 64).fieldOf("radius").orElse(10).forGetter((config) -> config.radius),
		Codec.intRange(1, 4).fieldOf("noise").orElse(4).forGetter((config) -> config.noise),
		Codec.doubleRange(0.0001, 128).fieldOf("multiplier").orElse(0.05).forGetter((config) -> config.multiplier),
		Codec.doubleRange(-1, 1).fieldOf("minThresh").orElse(0.2).forGetter((config) -> config.minThresh),
		Codec.doubleRange(-1, 1).fieldOf("maxThresh").orElse(1D).forGetter((config) -> config.maxThresh),
		Codec.BOOL.fieldOf("useY").orElse(false).forGetter((config) -> config.useY),
		Codec.BOOL.fieldOf("multiplyY").orElse(false).forGetter((config) -> config.multiplyY),
		Codec.BOOL.fieldOf("is3D").orElse(false).forGetter((config) -> config.is3D),
		Codec.BOOL.fieldOf("onlyExposed").orElse(false).forGetter((config) -> config.onlyExposed),
		RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter((config) -> config.replaceable),
		Codec.floatRange(0, 1).fieldOf("chance").orElse(1F).forGetter((config) -> config.chance)
	).apply(instance, PathSwapUnderWaterFeatureConfig::new));
}
