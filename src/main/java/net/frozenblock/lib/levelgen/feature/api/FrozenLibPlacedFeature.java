/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenLibPlacedFeature {
	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3
	 */
	public static final List<FrozenLibPlacedFeature> FEATURES = new ArrayList<>();

	@Getter
	private final ResourceKey<PlacedFeature> key;

	private Holder<Feature> configuredHolder;

	public FrozenLibPlacedFeature(Identifier key) {
		this.key = ResourceKey.create(Registries.PLACED_FEATURE, key);
		FEATURES.add(this);
	}

	public Holder<Feature> getConfiguredHolder() {
		assert this.configuredHolder.value() != null : "Trying get null holder from placed feature " + this.getKey().identifier();
		return this.configuredHolder;
	}

	public FrozenLibPlacedFeature setConfiguredHolder(Holder<Feature> configuredHolder) {
		this.configuredHolder = configuredHolder;
		return this;
	}

	public Holder<PlacedFeature> getHolder() {
		return FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT.lookup(Registries.PLACED_FEATURE).getOrThrow(this.getKey());
	}

	public WeightedPlacedFeature asWeightedPlacedFeature(float weight) {
		return new WeightedPlacedFeature(this.getHolder(), weight);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public FrozenLibPlacedFeature makeAndSetHolder(Holder<Feature> configuredHolder, List<PlacementModifier> modifiers) {
		this.setConfiguredHolder(configuredHolder);

		FrozenLibLogUtils.log("Registering placed feature " + this.getKey().identifier(), true);

		assert FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT != null : "Boostrap context is null when writing FrozenPlacedFeature " + this.getKey().identifier();
		assert configuredHolder != null : "Configured feature holder for FrozenPlacedFeature " + this.getKey().identifier() + " null";
		assert modifiers != null : "Placement modifiers for FrozenPlacedFeature " + this.getKey().identifier() + " null";

		FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT.register((ResourceKey) this.getKey(), new PlacedFeature(configuredHolder, modifiers));

		return this;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public FrozenLibPlacedFeature makeAndSetHolder(FrozenLibFeature configuredFeature, List<PlacementModifier> modifiers) {
		return this.makeAndSetHolder(configuredFeature.getHolder(), modifiers);
	}

	public FrozenLibPlacedFeature makeAndSetHolder(Holder<Feature> configuredHolder, PlacementModifier... modifiers) {
		return this.makeAndSetHolder(configuredHolder, List.of(modifiers));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public FrozenLibPlacedFeature makeAndSetHolder(FrozenLibFeature configuredFeature, PlacementModifier... modifiers) {
		return this.makeAndSetHolder(configuredFeature.getHolder(), List.of(modifiers));
	}
}
