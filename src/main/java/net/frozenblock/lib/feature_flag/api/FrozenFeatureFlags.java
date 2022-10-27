package net.frozenblock.lib.feature_flag.api;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.feature_flag.impl.FrozenFeatureFlagRegistryBuilder;

public class FrozenFeatureFlags {

	private static final Map<String, FrozenFeatureFlagRegistryBuilder> FEATURE_FLAG_BUILDERS = new HashMap<>();

	public static FrozenFeatureFlagRegistryBuilder createBuilder(ModContainer mod) {
		var modName = mod.getMetadata().getId();
		var builder = new FrozenFeatureFlagRegistryBuilder(modName);

		if (FEATURE_FLAG_BUILDERS.containsKey(modName) || FEATURE_FLAG_BUILDERS.containsValue(builder)) {
			throw new IllegalStateException("Cannot create a duplicate FeatureFlagRegistry.Builder");
		} else {
			FEATURE_FLAG_BUILDERS.put(modName, builder);
			return builder;
		}
	}
}
