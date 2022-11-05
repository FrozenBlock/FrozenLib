/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

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
