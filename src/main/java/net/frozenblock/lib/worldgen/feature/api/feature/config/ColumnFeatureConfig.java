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
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record ColumnFeatureConfig(
	BlockStateProvider blockStateProvider,
	BlockPredicate replaceable,
	IntProvider length,
	Direction direction,
	boolean stopWhenEncounteringUnreplaceableBlock
) implements FeatureConfiguration {
	public static final Codec<ColumnFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			BlockStateProvider.CODEC.fieldOf("block_state_provider").forGetter(config -> config.blockStateProvider),
			BlockPredicate.CODEC.fieldOf("replacement_block_predicate").forGetter(config -> config.replaceable),
			IntProvider.NON_NEGATIVE_CODEC.fieldOf("length").forGetter((config) -> config.length),
			Direction.CODEC.fieldOf("direction").forGetter(config -> config.direction),
			Codec.BOOL.lenientOptionalFieldOf("stop_when_encountering_unreplaceable_block", false).forGetter(config -> config.stopWhenEncounteringUnreplaceableBlock)
		).apply(instance, ColumnFeatureConfig::new));
}
