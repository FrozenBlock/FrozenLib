package net.frozenblock.lib.worldgen.feature.util;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricWorldgenProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenPlacementUtils {

	public static ResourceKey<PlacedFeature> createKey(String namespace, String path) {
		return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(namespace, path));
	}

	public static void register(
			FabricWorldgenProvider.Entries entries,
			ResourceKey<PlacedFeature> registryKey,
			Holder<ConfiguredFeature<?, ?>> holder,
			List<PlacementModifier> list
	) {
		entries.add(registryKey, new PlacedFeature(holder, List.copyOf(list)));
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
