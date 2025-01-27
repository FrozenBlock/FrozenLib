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

package net.frozenblock.lib.worldgen.feature.api;

import java.util.List;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

@UtilityClass
public class FrozenPlacementUtils {

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public static Holder<PlacedFeature> register(
		BootstrapContext<PlacedFeature> entries,
		ResourceKey<PlacedFeature> registryKey,
		Holder<ConfiguredFeature<?, ?>> holder,
		List<PlacementModifier> list
	) {
		return entries.register(registryKey, new PlacedFeature(holder, List.copyOf(list)));
	}

	public static Holder<PlacedFeature> register(
		BootstrapContext<PlacedFeature> entries,
		ResourceKey<PlacedFeature> registryKey,
		Holder<ConfiguredFeature<?, ?>> holder,
		PlacementModifier... placementModifiers
	) {
		return register(entries, registryKey, holder, List.of(placementModifiers));
	}

	public static Holder<PlacedFeature> getHolder(ResourceKey<PlacedFeature> resourceKey) {
		return VanillaRegistries.createLookup().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(resourceKey);
	}
}
