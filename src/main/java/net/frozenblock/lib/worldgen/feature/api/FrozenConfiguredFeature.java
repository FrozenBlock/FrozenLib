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
import java.util.Optional;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FrozenConfiguredFeature<FC extends FeatureConfiguration, C extends ConfiguredFeature<FC, ?>> {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenConfiguredFeature<?, ?>> FEATURES = new ArrayList<>();

	private final ResourceKey<ConfiguredFeature<?, ?>> key;

	public FrozenConfiguredFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
		FEATURES.add(this);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getKey() {
		return key;
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder() {
		return FrozenFeatureUtils.BOOTSTAP_CONTEXT.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey());
	}

	public Optional<ConfiguredFeature<?, ?>> getConfiguredFeature(LevelReader level) {
		Optional<ConfiguredFeature<?, ?>> returnedFeature = Optional.empty();
		Optional<HolderLookup.RegistryLookup<ConfiguredFeature<?, ?>>> configuredFeatures = level.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
		if (configuredFeatures.isPresent()) {
			Optional<Holder.Reference<ConfiguredFeature<?, ?>>> optionalConfiguredFeatureReference = configuredFeatures.get().get(this.getKey());
			if (optionalConfiguredFeatureReference.isPresent()) {
				returnedFeature = Optional.of(optionalConfiguredFeatureReference.get().value());
			}
		}
		return returnedFeature;
	}

	public ConfiguredFeature<?, ?> getConfiguredFeatureOrThrow(LevelReader level) {
		return level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey()).value();
	}

	@SuppressWarnings("unchecked")
	public <F extends Feature<FC>> FrozenConfiguredFeature<FC, C> makeAndSetHolder(F feature, FC config) {
		FrozenMain.log("Registering configured feature: " + this.getKey().location(), true);

		assert FrozenFeatureUtils.BOOTSTAP_CONTEXT != null: "Bootstrap context is null while registering " + this.getKey().location();

		assert feature != null: "Feature is null whilst registering " + this.getKey().location();
		assert config != null: "Feature configuration is null whilst registering " + this.getKey().location();

		FrozenFeatureUtils.BOOTSTAP_CONTEXT.register((ResourceKey) this.getKey(), new ConfiguredFeature<>(feature, config));
		return this;
	}
}
