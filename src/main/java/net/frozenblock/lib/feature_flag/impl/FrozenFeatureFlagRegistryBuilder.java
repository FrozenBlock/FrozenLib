package net.frozenblock.lib.feature_flag.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class FrozenFeatureFlagRegistryBuilder extends FeatureFlagRegistry.Builder {

	private boolean frozen;

	public FrozenFeatureFlagRegistryBuilder(String string) {
		super(string);
	}

	public boolean frozen() {
		return this.frozen;
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
