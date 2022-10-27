package net.frozenblock.lib.feature_flag.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class FrozenFeatureFlagRegistryBuilder extends FeatureFlagRegistry.Builder {

	private boolean frozen;
	private final String id;

	public FrozenFeatureFlagRegistryBuilder(String id) {
		super(id);
		this.id = id;
	}

	public boolean frozen() {
		return this.frozen;
	}

	public String getId() {
		return this.id;
	}

	public void freeze() {
		this.frozen = true;
	}

	@Override
	public FeatureFlag create(ResourceLocation flagLocation) {
		if (this.frozen) {
			throw new IllegalStateException("Cannot add FeatureFlags to a frozen FeatureFlagRegistry.Builder");
		} else {
			return super.create(flagLocation);
		}
	}

	@Override
	public FeatureFlagRegistry build() {
		if (this.frozen) {
			throw new IllegalStateException("Cannot build a FeatureFlagRegistry.Builder more than once.");
		} else {
			this.freeze();
			return super.build();
		}
	}
}
