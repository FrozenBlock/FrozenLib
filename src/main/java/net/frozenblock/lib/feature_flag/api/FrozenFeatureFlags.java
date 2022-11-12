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

import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoints.CommonEventEntrypoint;
import net.frozenblock.lib.events.api.FrozenEvents;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class FrozenFeatureFlags {

	public static final Event<FeatureFlagInitEntrypoint> ON_FEATURE_FLAG_INIT = FrozenEvents.createEnvironmentEvent(FeatureFlagInitEntrypoint.class,
			callbacks -> (builder) -> {
				for (var callback : callbacks) {
					callback.init(builder);
				}
			});

	@FunctionalInterface
	public interface FeatureFlagInitEntrypoint extends CommonEventEntrypoint {
		void init(FeatureFlagRegistry.Builder builder);
	}
}
