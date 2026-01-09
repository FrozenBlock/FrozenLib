/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record CurvingSpikeConfig(
	BlockStateProvider stateProvider, IntProvider xWidth, IntProvider zWidth, IntProvider height, FloatProvider curveDistance, BlockPredicate replaceable
) implements FeatureConfiguration {
	public static final Codec<CurvingSpikeConfig> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			BlockStateProvider.CODEC.fieldOf("state").forGetter(CurvingSpikeConfig::stateProvider),
			IntProvider.codec(1, 12).fieldOf("x_width").forGetter(CurvingSpikeConfig::xWidth),
			IntProvider.codec(1, 12).fieldOf("z_width").forGetter(CurvingSpikeConfig::zWidth),
			IntProvider.codec(1, 32).fieldOf("height").forGetter(CurvingSpikeConfig::height),
			FloatProvider.codec(-4F, 4F).fieldOf("curve_distance").forGetter(CurvingSpikeConfig::curveDistance),
			BlockPredicate.CODEC.fieldOf("replaceable").forGetter(CurvingSpikeConfig::replaceable)
		).apply(instance, CurvingSpikeConfig::new)
	);
}
