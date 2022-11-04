package net.frozenblock.lib.worldgen.feature;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used for easier registry of features.
 * @since 1.1.3
 */

@ApiStatus.Experimental
public class FrozenPlacedFeature {

	public final ResourceKey<PlacedFeature> resourceKey;
	public final ResourceKey<ConfiguredFeature<?, ?>> featureKey;
	public final PlacementModifier[] placementModifiers;


	public FrozenPlacedFeature(ResourceKey<PlacedFeature> resourceKey, ResourceKey<ConfiguredFeature<?, ?>> featureKey, PlacementModifier[] placementModifiers) {
		this.resourceKey = resourceKey;
		this.featureKey = featureKey;
		this.placementModifiers = placementModifiers;
	}
}
