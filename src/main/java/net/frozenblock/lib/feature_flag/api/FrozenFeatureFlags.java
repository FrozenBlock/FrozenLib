package net.frozenblock.lib.feature_flag.api;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.events.api.FrozenEvents;
import net.frozenblock.lib.feature_flag.impl.FrozenFeatureFlagRegistryBuilder;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class FrozenFeatureFlags {

	private static final Map<String, FrozenFeatureFlagRegistryBuilder> FEATURE_FLAG_BUILDERS = new HashMap<>();

	public static final Event<FeatureFlagInitEntrypoint> ON_FEATURE_FLAG_INIT = FrozenEvents.createEnvironmentEvent(FeatureFlagInitEntrypoint.class,
			callbacks -> (builder) -> {
				for (var callback : callbacks) {
					callback.init(builder);
				}
			});

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

	@FunctionalInterface
	public interface FeatureFlagInitEntrypoint extends CommonEventEntrypoint {
		void init(FeatureFlagRegistry.Builder builder);
	}
}
