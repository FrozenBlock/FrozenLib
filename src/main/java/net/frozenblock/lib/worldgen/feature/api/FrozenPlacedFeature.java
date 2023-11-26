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

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenPlacedFeature {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenPlacedFeature> FEATURES = new ArrayList<>();

	private final ResourceKey<PlacedFeature> key;

	private Holder<ConfiguredFeature<?, ?>> configuredHolder;

	public FrozenPlacedFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.PLACED_FEATURE, key);
		FEATURES.add(this);
	}
	public ResourceKey<PlacedFeature> getKey() {
		return key;
	}

	public Holder<ConfiguredFeature<?, ?>> getConfiguredHolder() {
		assert this.configuredHolder.value() != null: "Trying get null holder from placed feature " + this.getKey().location();
		return this.configuredHolder;
	}

	public FrozenPlacedFeature setConfiguredHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder) {
		this.configuredHolder = configuredHolder;
		return this;
	}

	public Holder<PlacedFeature> getHolder() {
		return FrozenFeatureUtils.BOOTSTAP_CONTEXT.lookup(Registries.PLACED_FEATURE).getOrThrow(this.getKey());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, List<PlacementModifier> modifiers) {
		setConfiguredHolder(configuredHolder);

		FrozenLogUtils.log("Registering placed feature " + this.getKey().location(), true);

		assert FrozenFeatureUtils.BOOTSTAP_CONTEXT != null: "Boostrap context is null when writing FrozenPlacedFeature " + this.getKey().location();
		assert configuredHolder != null: "Configured feature holder for FrozenPlacedFeature " + this.getKey().location() + " null";
		assert modifiers != null: "Placement modifiers for FrozenPlacedFeature " + this.getKey().location() + " null";

		FrozenFeatureUtils.BOOTSTAP_CONTEXT.register((ResourceKey) this.getKey(), new PlacedFeature(configuredHolder, modifiers));

		return this;
	}

	public <FC extends FeatureConfiguration> FrozenPlacedFeature makeAndSetHolder(Holder<ConfiguredFeature<?, ?>> configuredHolder, PlacementModifier... modifiers) {
		return this.makeAndSetHolder(configuredHolder, List.of(modifiers));
	}
}
