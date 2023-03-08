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
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FrozenPlacedFeature {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenPlacedFeature> FEATURES = new ArrayList<>();

	public static BootstapContext<PlacedFeature> BOOTSTAP_CONTEXT = null;

	private final ResourceKey<PlacedFeature> key;

	private Holder<ConfiguredFeature<?, ?>> configuredHolder;
	private Holder<PlacedFeature> holder;

	public FrozenPlacedFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.PLACED_FEATURE, key);
		FEATURES.add(this);
	}
	public ResourceKey<PlacedFeature> getKey() {
		return key;
	}

	public Holder<@Nullable ConfiguredFeature<?, ?>> getConfiguredHolder() {
		if (this.configuredHolder == null)
			return Holder.direct(null);
		return this.configuredHolder;
	}

	@SuppressWarnings("unchecked")
	public <FC extends FeatureConfiguration> FrozenPlacedFeature setConfiguredHolder(Holder<ConfiguredFeature<FC, ?>> configuredHolder) {
		this.configuredHolder = (Holder) configuredHolder;
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, List<PlacementModifier> modifiers) {
		this.configuredHolder = (Holder) configuredHolder;
		Holder<PlacedFeature> holder = BOOTSTAP_CONTEXT.register(this.getKey(), new PlacedFeature(configuredHolder, modifiers));
		return this.setHolder(holder);
	}

	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, PlacementModifier... modifiers) {
		return this.makeAndSetHolder(configuredHolder, List.of(modifiers));
	}
}
