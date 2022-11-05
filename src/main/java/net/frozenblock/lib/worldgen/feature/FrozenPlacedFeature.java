package net.frozenblock.lib.worldgen.feature;

import net.frozenblock.lib.worldgen.feature.util.FrozenConfiguredFeatureUtils;
import net.frozenblock.lib.worldgen.feature.util.FrozenPlacementUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.ApiStatus;
import java.util.List;

/**
 * Used for easier registry of features.
 * @since 1.1.3
 */

@ApiStatus.Experimental
public class FrozenPlacedFeature {

	private final ResourceKey<PlacedFeature> resourceKey;
	private final ResourceKey<ConfiguredFeature<?, ?>> featureKey;
	private final List<PlacementModifier> placementModifiers;
	private final PlacedFeature placedFeature;


	public FrozenPlacedFeature(ResourceKey<PlacedFeature> resourceKey, ResourceKey<ConfiguredFeature<?, ?>> featureKey, List<PlacementModifier> placementModifiers) {
		this.resourceKey = resourceKey;
		this.featureKey = featureKey;
		this.placementModifiers = placementModifiers;
		this.placedFeature = new PlacedFeature(FrozenConfiguredFeatureUtils.getHolder(this.getFeatureKey()), this.placementModifiers);
	}

	public ResourceKey<PlacedFeature> getResourceKey() {
		return this.resourceKey;
	}

	public PlacedFeature getPlacedFeature() {
		return this.placedFeature;
	}

	public Holder<PlacedFeature> getHolder() {
		return FrozenPlacementUtils.getHolder(this.getResourceKey());
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getFeatureKey() {
		return this.featureKey;
	}

	public List<PlacementModifier> getPlacementModifiers() {
		return this.placementModifiers;
	}
}
