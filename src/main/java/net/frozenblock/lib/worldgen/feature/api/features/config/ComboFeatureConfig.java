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
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record ComboFeatureConfig(Holder<PlacedFeature> featureA, Holder<PlacedFeature> featureB) implements FeatureConfiguration {
	public static final Codec<ComboFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
		PlacedFeature.CODEC.fieldOf("feature").forGetter(vegetationPatchConfiguration -> vegetationPatchConfiguration.featureA),
		PlacedFeature.CODEC.fieldOf("second_feature").forGetter(vegetationPatchConfiguration -> vegetationPatchConfiguration.featureB)
	).apply(instance, ComboFeatureConfig::new));
}
