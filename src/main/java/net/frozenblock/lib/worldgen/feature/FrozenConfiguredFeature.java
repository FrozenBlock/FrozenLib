package net.frozenblock.lib.worldgen.feature;

import net.frozenblock.lib.worldgen.feature.util.FrozenConfiguredFeatureUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used for easier registry of features.
 * @since 1.1.3
 */

@ApiStatus.Experimental
public class FrozenConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>> {

	private final ResourceKey<ConfiguredFeature<?, ?>> resourceKey;
	private final F feature;
	private final FC featureConfiguration;


	public FrozenConfiguredFeature(ResourceKey<ConfiguredFeature<?, ?>> resourceKey, F feature, FC featureConfiguration) {
		this.resourceKey = resourceKey;
		this.feature = feature;
		this.featureConfiguration = featureConfiguration;
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getResourceKey() {
		return this.resourceKey;
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder() {
		return FrozenConfiguredFeatureUtils.getHolder(this.getResourceKey());
	}

	public F getFeature() {
		return this.feature;
	}

	public FC getFeatureConfiguration() {
		return this.featureConfiguration;
	}
}
