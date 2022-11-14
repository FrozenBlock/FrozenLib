/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.api;

import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricWorldgenProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public final class FrozenPlacementUtils {
	private FrozenPlacementUtils() {
		throw new UnsupportedOperationException("FrozenPlacementUtils contains only static declarations.");
	}

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(namespace, path));
	}

	public static Holder<PlacedFeature> register(
			FabricWorldgenProvider.Entries entries,
			ResourceKey<PlacedFeature> registryKey,
			Holder<ConfiguredFeature<?, ?>> holder,
			List<PlacementModifier> list
	) {
		return entries.add(registryKey, new PlacedFeature(holder, List.copyOf(list)));
	}

	public static void register(
			FabricWorldgenProvider.Entries entries,
			ResourceKey<PlacedFeature> registryKey,
			Holder<ConfiguredFeature<?, ?>> holder,
			PlacementModifier... placementModifiers
	) {
		register(entries, registryKey, holder, List.of(placementModifiers));
	}

	public static Holder<PlacedFeature> getHolder(ResourceKey<PlacedFeature> resourceKey) {
		return VanillaRegistries.createLookup().lookupOrThrow(Registry.PLACED_FEATURE_REGISTRY).getOrThrow(resourceKey);
	}
}
