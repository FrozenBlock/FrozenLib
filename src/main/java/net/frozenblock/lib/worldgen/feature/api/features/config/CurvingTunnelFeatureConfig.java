/*
 * Copyright (C) 2024 FrozenBlock
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
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record CurvingTunnelFeatureConfig(BlockStateProvider state, int radius, double minCurvature, double maxCurvature, TagKey<Block> replaceableBlocks) implements FeatureConfiguration {
	public static final Codec<CurvingTunnelFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		BlockStateProvider.CODEC.fieldOf("state").forGetter((config) -> config.state),
		Codec.intRange(1, 64).fieldOf("radius").orElse(3).forGetter((config) -> config.radius),
		Codec.doubleRange(0D, 15D).fieldOf("min_curvature").orElse(3D).forGetter((config) -> config.minCurvature),
		Codec.doubleRange(0D, 15D).fieldOf("max_curvature").orElse(3D).forGetter((config) -> config.maxCurvature),
		TagKey.codec(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter((config) -> config.replaceableBlocks)
	).apply(instance, CurvingTunnelFeatureConfig::new));
}
