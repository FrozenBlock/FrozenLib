/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.feature.api;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class FrozenPlacedFeature {

	private final ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
	private final ResourceKey<PlacedFeature> key;

	private Holder<ConfiguredFeature<?, ?>> configuredHolder;
	private Holder<PlacedFeature> holder;

	public FrozenPlacedFeature(ResourceLocation configuredKey, ResourceLocation key) {
		this.configuredKey = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, configuredKey);
		this.key = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, key);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getConfiguredKey() {
		return configuredKey;
	}

	public ResourceKey<PlacedFeature> getKey() {
		return key;
	}

	public Holder<@Nullable ConfiguredFeature<?, ?>> getConfiguredHolder() {
		if (this.configuredHolder == null)
			return Holder.direct(null);
		return this.configuredHolder;
	}

	public FrozenPlacedFeature setConfiguredHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder) {
		this.configuredHolder = configuredHolder;
		return this;
	}

	public Holder<@Nullable PlacedFeature> getHolder() {
		if (this.holder == null)
			return Holder.direct(null);
		return this.holder;
	}

	public FrozenPlacedFeature setHolder(Holder<PlacedFeature> holder) {
		this.holder = holder;
		return this;
	}
}
