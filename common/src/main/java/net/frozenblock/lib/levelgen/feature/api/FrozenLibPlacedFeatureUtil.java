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

package net.frozenblock.lib.levelgen.feature.api;

import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

@UtilityClass
public class FrozenLibPlacedFeatureUtil {

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(namespace, path));
	}

	public static Holder<PlacedFeature> register(
		BootstrapContext<PlacedFeature> entries,
		ResourceKey<PlacedFeature> registryKey,
		Holder<Feature> feature,
		List<PlacementModifier> list
	) {
		return entries.register(registryKey, new PlacedFeature(feature, List.copyOf(list)));
	}

	public static Holder<PlacedFeature> register(
		BootstrapContext<PlacedFeature> entries,
		ResourceKey<PlacedFeature> registryKey,
		Holder<Feature> feature,
		PlacementModifier... placementModifiers
	) {
		return register(entries, registryKey, feature, List.of(placementModifiers));
	}

	public static Holder<PlacedFeature> register(
		DynamicRegistryManagerSetupContext entries,
		ResourceKey<PlacedFeature> registryKey,
		ResourceKey<Feature> configuredKey,
		List<PlacementModifier> list
	) {
		final DynamicRegistryManagerSetupContext.RegistryMap registry = entries.getRegistries(Set.of(Registries.FEATURE, Registries.PLACED_FEATURE));
		final Holder<Feature> feature = entries.registryManager().lookupOrThrow(Registries.FEATURE).getOrThrow(configuredKey);
		final PlacedFeature placed = registry.register(Registries.PLACED_FEATURE, registryKey.identifier(), new PlacedFeature(feature, List.copyOf(list)));
		return Holder.direct(placed);
	}

	public static Holder<PlacedFeature> register(
		DynamicRegistryManagerSetupContext entries,
		ResourceKey<PlacedFeature> registryKey,
		ResourceKey<Feature> resourceKey,
		PlacementModifier... placementModifiers
	) {
		return register(entries, registryKey, resourceKey, List.of(placementModifiers));
	}

	// TODO: see if this 26.3 change affects anything
	public static Holder<PlacedFeature> getHolder(ResourceKey<PlacedFeature> resourceKey) {
		return VanillaRegistries.createWorldLookup().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(resourceKey);
	}
}
