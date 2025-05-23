/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.Nullable;

public class FrozenLibConfiguredFeature<FC extends FeatureConfiguration> {

	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3+.
	 */
	public static final List<FrozenLibConfiguredFeature<?>> FEATURES = new ArrayList<>();

	private final ResourceKey<ConfiguredFeature<?, ?>> key;

	public FrozenLibConfiguredFeature(ResourceLocation key) {
		this.key = ResourceKey.create(Registries.CONFIGURED_FEATURE, key);
		FEATURES.add(this);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getKey() {
		return key;
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder(@Nullable LevelReader level) {
		if (level == null) return FrozenLibFeatureUtils.BOOTSTRAP_CONTEXT.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey());
		return level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(this.getKey());
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder() {
		return getHolder(null);
	}

	public ConfiguredFeature<?, ?> getConfiguredFeature(LevelReader level) {
		return getHolder(level).value();
	}

	@SuppressWarnings("unchecked")
	public <F extends Feature<FC>> FrozenLibConfiguredFeature<FC> makeAndSetHolder(F feature, FC config) {
		FrozenLibLogUtils.log("Registering configured feature: " + this.getKey().location(), FrozenLibLogUtils.UNSTABLE_LOGGING);

		assert FrozenLibFeatureUtils.BOOTSTRAP_CONTEXT != null : "Bootstrap context is null while registering " + this.getKey().location();

		assert feature != null : "Feature is null whilst registering " + this.getKey().location();
		assert config != null : "Feature configuration is null whilst registering " + this.getKey().location();

		FrozenLibFeatureUtils.BOOTSTRAP_CONTEXT.register((ResourceKey) this.getKey(), new ConfiguredFeature<>(feature, config));
		return this;
	}
}
