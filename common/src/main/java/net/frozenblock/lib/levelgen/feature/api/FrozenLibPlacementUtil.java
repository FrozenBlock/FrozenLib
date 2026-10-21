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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.DynamicRegistryManagerSetupContext;

@UtilityClass
public final class FrozenLibPlacementUtil {

	@Unmodifiable
	public static List<PlacementModifier> postModifiers(List<PlacementModifier> originalModifiers, PlacementModifier... postModifiers) {
		final List<PlacementModifier> finalModifiers = new ArrayList<>();
		finalModifiers.addAll(originalModifiers);
		finalModifiers.addAll(Arrays.stream(postModifiers).toList());
		return List.copyOf(finalModifiers);
	}

	@Unmodifiable
	public static List<PlacementModifier> postModifiers(List<PlacementModifier> originalModifiers, List<PlacementModifier> postModifiers) {
		return postModifiers(originalModifiers, postModifiers.toArray(new PlacementModifier[0]));
	}

	@Unmodifiable
	public static List<PlacementModifier> postModifiersWithPreFilter(PlacementFilter preFilter, List<PlacementModifier> originalModifiers, PlacementModifier... postModifiers) {
		final List<PlacementModifier> originalModifiersWithPreFilter = new ArrayList<>();
		originalModifiersWithPreFilter.addFirst(preFilter);
		originalModifiersWithPreFilter.addAll(originalModifiers);
		return postModifiers(originalModifiersWithPreFilter, postModifiers);
	}

	@Unmodifiable
	public static List<PlacementModifier> modifiersWithFilter(@Nullable PlacementFilter generationPredicate, PlacementModifier countModifier, PlacementModifier... modifiers) {
		final List<PlacementModifier> finalModifiers = new ArrayList<>();
		if (generationPredicate != null) finalModifiers.add(generationPredicate);
		finalModifiers.add(countModifier);
		finalModifiers.add(InSquarePlacement.spread());
		finalModifiers.addAll(Arrays.stream(modifiers).toList());
		finalModifiers.add(BiomeFilter.biome());
		return List.copyOf(finalModifiers);
	}

	@Unmodifiable
	public static List<PlacementModifier> modifiersWithFilter(Holder<ConfigPredicate> generationPredicate, PlacementModifier countModifier, PlacementModifier... modifiers) {
		return modifiersWithFilter(ConfigPredicate.placementFilter(generationPredicate), countModifier, modifiers);
	}

	@Unmodifiable
	public static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier... modifiers) {
		return modifiersWithFilter((PlacementFilter) null, countModifier, modifiers);
	}

	@Unmodifiable
	public static List<PlacementModifier> modifiers(PlacementModifier countModifier, List<PlacementModifier> modifiers) {
		return modifiers(countModifier, modifiers.toArray(new PlacementModifier[0]));
	}

	@Unmodifiable
	public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier... modifiers) {
		return modifiers(CountPlacement.of(count), modifiers);
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

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(namespace, path));
	}

	public static Holder<PlacedFeature> getHolder(ResourceKey<PlacedFeature> resourceKey) {
		return VanillaRegistries.createWorldLookup().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(resourceKey);
	}
}
