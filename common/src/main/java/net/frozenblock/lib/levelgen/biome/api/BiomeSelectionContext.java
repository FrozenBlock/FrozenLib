/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.biome.api;

/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 */
public interface BiomeSelectionContext {
	ResourceKey<Biome> getBiomeKey();

	/**
	 * @return the biome with modifications by biome modifiers of higher priority already applied.
	 */
	Biome getBiome();

	Holder<Biome> getBiomeHolder();

	/**
	 * @return a list of all features in this biome, ordered by {@link GenerationStep}.
	 */
	default List<HolderSet<PlacedFeature>> featureSteps() {
		return getBiome().getGenerationSettings().features();
	}

	/**
	 * @return true if this biome contains a placed feature matching the given {@link Predicate}.
	 */
	default boolean hasPlacedFeature(Predicate<Holder<PlacedFeature>> predicate) {
		List<HolderSet<PlacedFeature>> featureSteps = getBiome().getGenerationSettings().features();

		for (HolderSet<PlacedFeature> features : featureSteps) {
			for (Holder<PlacedFeature> featureHolder : features) {
				if (predicate.test(featureHolder)) return true;
			}
		}

		return false;
	}

	/**
	 * @return true if this biome contains a placed feature referencing a configured feature with the given key.
	 */
	default boolean hasFeature(ResourceKey<Feature> key) {
		return hasPlacedFeature(featureHolder ->
			featureHolder.value()
				.getFeatures()
				.anyMatch(feature -> getFeatureKey(feature.value()).orElse(null) == key)
		);
	}

	/**
	 * @return true if this biome contains a placed feature with the given key.
	 */
	default boolean hasPlacedFeature(ResourceKey<PlacedFeature> key) {
		return hasPlacedFeature(featureHolder -> getPlacedFeatureKey(featureHolder.value()).orElse(null) == key);
	}

	/**
	 * Tries to retrieve the resource key for the given configured feature, which should be from this biomes
	 * current feature list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<ResourceKey<Feature>> getFeatureKey(Feature feature);

	/**
	 * Tries to retrieve the resource key for the given placed feature, which should be from this biomes
	 * current feature list. May be empty if the placed feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature);

	/**
	 * Returns true if the configured structure with the given key can start in this biome in any chunk generator
	 * used by the current level.
	 */
	boolean validForStructure(ResourceKey<Structure> key);

	/**
	 * Tries to retrieve the resource key for the given configured feature, which should be from this biomes
	 * current structure list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<ResourceKey<Structure>> getStructureKey(Structure structure);

	/**
	 * Tries to determine whether this biome generates in a specific dimension, based on the {@link WorldOptions}
	 * used by the current level.
	 *
	 * <p>If no level stem exists for the given level stem key, <code>false</code> is returned.
	 */
	boolean canGenerateIn(ResourceKey<LevelStem> key);

	/**
	 * @return true if this biome is in the given {@link TagKey}.
	 */
	boolean hasTag(TagKey<Biome> tag);
}
