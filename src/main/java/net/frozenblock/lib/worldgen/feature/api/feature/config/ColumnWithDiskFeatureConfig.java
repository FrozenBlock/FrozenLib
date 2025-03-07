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
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ColumnWithDiskFeatureConfig(
	BlockState state,
	IntProvider radius,
	IntProvider height,
	float surroundingPillarChance,
	HolderSet<Block> replaceableBlocks,
	HolderSet<Block> diskBlocks
) implements FeatureConfiguration {
	public static final Codec<ColumnWithDiskFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
		instance.group(
			BlockState.CODEC.fieldOf("state").forGetter((config) -> config.state),
			IntProvider.NON_NEGATIVE_CODEC.fieldOf("radius").forGetter((config) -> config.radius),
			IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter((config) -> config.height),
			Codec.FLOAT.fieldOf("surrounding_pillar_chance").forGetter((config) -> config.surroundingPillarChance),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable_blocks").forGetter((config) -> config.replaceableBlocks),
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("disk_blocks").forGetter((config) -> config.diskBlocks)
		).apply(instance, ColumnWithDiskFeatureConfig::new));
}
