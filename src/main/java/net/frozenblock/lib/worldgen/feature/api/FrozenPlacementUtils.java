/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.feature.api;

import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

public final class FrozenPlacementUtils {
	private FrozenPlacementUtils() {
		throw new UnsupportedOperationException("FrozenPlacementUtils contains only static declarations.");
	}

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(namespace, path));
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

	public static Holder<PlacedFeature> register(
			DynamicRegistryManagerSetupContext entries,
			ResourceKey<PlacedFeature> registryKey,
			ResourceKey<ConfiguredFeature<?, ?>> configuredKey,
			List<PlacementModifier> list
	) {
		var registry = entries.getRegistries(Set.of(Registries.CONFIGURED_FEATURE, Registries.PLACED_FEATURE));
		var configured = entries.registryManager().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(configuredKey);
		var value = registry.register(Registries.PLACED_FEATURE, registryKey.location(), new PlacedFeature(configured, List.copyOf(list)));
		return Holder.direct(value);
	}

	public static Holder<PlacedFeature> register(
			DynamicRegistryManagerSetupContext entries,
			ResourceKey<PlacedFeature> registryKey,
			ResourceKey<ConfiguredFeature<?, ?>> resourceKey,
			PlacementModifier... placementModifiers
	) {
		return register(entries, registryKey, resourceKey, List.of(placementModifiers));
	}

	public static Holder<PlacedFeature> getHolder(ResourceKey<PlacedFeature> resourceKey) {
		return VanillaRegistries.createLookup().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(resourceKey);
	}
}
