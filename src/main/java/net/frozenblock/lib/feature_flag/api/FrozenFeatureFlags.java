package net.frozenblock.lib.feature_flag.api;

import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;

public class FrozenFeatureFlags {

	private FrozenFeatureFlags() {
		throw new UnsupportedOperationException("FrozenFeatureFlags contains only static declarations.");
	}

	public static FeatureFlagRegistry.Builder builder;

	public static void rebuild() {
		FeatureFlags.REGISTRY = builder.build();
		FeatureFlags.CODEC = FeatureFlags.REGISTRY.codec();
	}
}
