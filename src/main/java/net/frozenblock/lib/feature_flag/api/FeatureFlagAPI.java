/*
 * Copyright (C) 2024 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.feature_flag.api;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to help with custom {@link net.minecraft.world.flag.FeatureFlag}s.
 */
@UtilityClass
public class FeatureFlagAPI {
	@ApiStatus.Internal
	public static FeatureFlagRegistry.Builder builder;

	public static void rebuild() {
		Preconditions.checkArgument(builder != null, new NullPointerException("Feature flags rebuilt before builder exists"));
		FeatureFlags.REGISTRY = builder.build();
		FeatureFlags.CODEC = FeatureFlags.REGISTRY.codec();
	}
}
