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

package net.frozenblock.lib.levelgen.biome.api.modifications;

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

import com.google.common.base.Preconditions;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Provides an API to modify Biomes after they have been loaded and before they are used in the Level.
 *
 * <p>Any modifications made to biomes will not be available for use in the demo level.
 */
@UtilityClass
public final class BiomeModifications {

	/**
	 * Convenience method to add a feature to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addFeature(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Decoration step, ResourceKey<PlacedFeature> placedFeatureKey) {
		create(placedFeatureKey.identifier()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addFeature(step, placedFeatureKey);
		});
	}

	/**
	 * Convenience method to add a carver to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addCarver(Predicate<BiomeSelectionContext> biomeSelector, ResourceKey<ConfiguredWorldCarver<?>> configuredCarverKey) {
		create(configuredCarverKey.identifier()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addCarver(configuredCarverKey);
		});
	}

	/**
	 * Convenience method to add an entity spawn to one or more biomes.
	 *
	 * @see BiomeSelectors
	 * @see net.minecraft.world.level.biome.MobSpawnSettings.Builder#addSpawn(MobCategory, int, MobSpawnSettings.SpawnerData)
	 */
	public static void addSpawn(
		Predicate<BiomeSelectionContext> biomeSelector,
		MobCategory category,
		EntityType<?> entityType,
		int weight,
		int minGroupSize,
		int maxGroupSize
	) {
		// See constructor of SpawnSettings.SpawnEntry for context
		Preconditions.checkArgument(
			entityType.getCategory() != MobCategory.MISC,
			"Cannot add spawns for entities with category=MISC since they'd be replaced by pigs."
		);

		// We need the entity type to be registered, or we cannot deduce an ID otherwise
		final Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		Preconditions.checkState(BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType).isPresent(), "Unregistered entity type: %s", entityType);

		create(entityId).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(entityType, minGroupSize, maxGroupSize), weight);
		});
	}

	/**
	 * Creates a new biome modification which will be applied whenever biomes are loaded from data packs.
	 *
	 * @param id An identifier for the new set of biome modifications that is returned. Is used for
	 *           guaranteeing consistent ordering between the biome modifications added by different mods
	 *           (assuming they otherwise have the same phase).
	 */
	public static BiomeModification create(Identifier id) {
		return new BiomeModification(id);
	}
}
