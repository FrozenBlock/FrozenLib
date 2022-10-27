package net.frozenblock.lib.feature_flag.api;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.feature_flag.impl.FrozenFeatureFlagRegistryBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;

public class FrozenFeatureFlags {

	private static final Map<String, FrozenFeatureFlagRegistryBuilder> FEATURE_FLAG_BUILDERS = new HashMap<>();

	public static FrozenFeatureFlagRegistryBuilder createBuilder(ModContainer mod) {
		var modName = mod.getMetadata().getId();
		var builder = new FrozenFeatureFlagRegistryBuilder(modName);

		if (FEATURE_FLAG_BUILDERS.containsKey(modName) || FEATURE_FLAG_BUILDERS.containsValue(builder)) {
			throw new IllegalStateException("Cannot create more than one FeatureFlagRegistry.Builder");
		} else {
			FEATURE_FLAG_BUILDERS.put(modName, builder);
			return new FrozenFeatureFlagRegistryBuilder(mod.toString());
		}
	}

	public static FeatureFlag createFlag(FrozenFeatureFlagRegistryBuilder builder, ResourceLocation flagLocation) {
		if (FEATURE_FLAG_BUILDERS.containsValue(builder)) {
			return builder.create(flagLocation);
		} else {
			throw new IllegalStateException("A FeatureFlagRegistry.Builder must be registered to create a flag.");
		}
	}
}
