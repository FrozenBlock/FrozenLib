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

package net.frozenblock.lib.levelgen.feature.api.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.data.ConfigEntryPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record ConfigEntrySelectorFeatureConfiguration(
	ConfigEntryPredicate<?> configEntryPredicate,
	Holder<PlacedFeature> featureIfTrue,
	Holder<PlacedFeature> featureIfFalse
) implements FeatureConfiguration {
	public static final Codec<ConfigEntrySelectorFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ConfigEntryPredicate.CODEC.fieldOf("config_entry_predicate").forGetter(config -> config.configEntryPredicate),
		PlacedFeature.CODEC.fieldOf("feature_if_true").forGetter(config -> config.featureIfTrue),
		PlacedFeature.CODEC.fieldOf("feature_if_false").forGetter(config -> config.featureIfFalse)
	).apply(instance, ConfigEntrySelectorFeatureConfiguration::new));
}
