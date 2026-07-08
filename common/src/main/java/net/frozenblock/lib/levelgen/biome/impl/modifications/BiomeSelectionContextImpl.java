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

package net.frozenblock.lib.levelgen.biome.impl.modifications;

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

import java.util.Optional;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

public class BiomeSelectionContextImpl implements BiomeSelectionContext {
	private final RegistryAccess dynamicRegistries;
	private final ResourceKey<Biome> key;
	private final Biome biome;
	private final Holder<Biome> entry;

	public BiomeSelectionContextImpl(RegistryAccess dynamicRegistries, ResourceKey<Biome> key, Biome biome) {
		this.dynamicRegistries = dynamicRegistries;
		this.key = key;
		this.biome = biome;
		this.entry = dynamicRegistries.lookupOrThrow(Registries.BIOME).getOrThrow(this.key);
	}

	@Override
	public ResourceKey<Biome> getBiomeKey() {
		return key;
	}

	@Override
	public Biome getBiome() {
		return biome;
	}

	@Override
	public Holder<Biome> getBiomeHolder() {
		return entry;
	}

	@Override
	public Optional<ResourceKey<Feature>> getFeatureKey(Feature feature) {
		final Registry<Feature> registry = this.dynamicRegistries.lookupOrThrow(Registries.FEATURE);
		return registry.getResourceKey(feature);
	}

	@Override
	public Optional<ResourceKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
		final Registry<PlacedFeature> registry = this.dynamicRegistries.lookupOrThrow(Registries.PLACED_FEATURE);
		return registry.getResourceKey(placedFeature);
	}

	@Override
	public boolean validForStructure(ResourceKey<Structure> key) {
		final Structure instance = this.dynamicRegistries.lookupOrThrow(Registries.STRUCTURE).getValue(key);
		if (instance == null) return false;
		return instance.biomes().contains(getBiomeHolder());
	}

	@Override
	public Optional<ResourceKey<Structure>> getStructureKey(Structure structure) {
		final Registry<Structure> registry = this.dynamicRegistries.lookupOrThrow(Registries.STRUCTURE);
		return registry.getResourceKey(structure);
	}

	@Override
	public boolean canGenerateIn(ResourceKey<LevelStem> key) {
		final LevelStem dimension = this.dynamicRegistries.lookupOrThrow(Registries.LEVEL_STEM).getValue(key);
		if (dimension == null) return false;
		return dimension.generator().getBiomeSource().possibleBiomes().stream().anyMatch(entry -> entry.value() == biome);
	}

	@Override
	public boolean hasTag(TagKey<Biome> tag) {
		final Registry<Biome> biomeRegistry = this.dynamicRegistries.lookupOrThrow(Registries.BIOME);
		return biomeRegistry.getOrThrow(getBiomeKey()).is(tag);
	}
}
