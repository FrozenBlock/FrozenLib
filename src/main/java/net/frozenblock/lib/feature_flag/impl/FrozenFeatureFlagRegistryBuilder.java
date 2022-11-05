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
