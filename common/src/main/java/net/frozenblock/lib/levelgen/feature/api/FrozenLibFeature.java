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
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.platform.api.registry.FrozenHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.jetbrains.annotations.Nullable;

public class FrozenLibFeature {
	/**
	 * Can be used for setting all bootstrap contexts on 1.19.3+.
	 */
	public static final List<FrozenLibFeature> FEATURES = new ArrayList<>();

	private final ResourceKey<Feature> key;

	public FrozenLibFeature(Identifier key) {
		this.key = ResourceKey.create(Registries.FEATURE, key);
		FEATURES.add(this);
	}

	public ResourceKey<Feature> getKey() {
		return this.key;
	}

	public Holder<Feature> getHolder(@Nullable LevelReader level) {
		if (level == null) return FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT.lookup(Registries.FEATURE).getOrThrow(this.getKey());
		return level.registryAccess().lookupOrThrow(Registries.FEATURE).getOrThrow(this.getKey());
	}

	public Holder<Feature> getHolder() {
		return getHolder(null);
	}

	public Feature getFeature(LevelReader level) {
		return getHolder(level).value();
	}

	public WeightedPlacedFeature asWeightedPlacedFeature(float weight, PlacementModifier... placementModifiers) {
		return new WeightedPlacedFeature(this.asInlinePlaced(placementModifiers), weight);
	}

	public Holder<PlacedFeature> asInlinePlaced(PlacementModifier... placementModifiers) {
		return PlacementUtils.inlinePlaced(this.getHolder(), placementModifiers);
	}

	@SuppressWarnings("unchecked")
	public FrozenLibFeature makeAndSetHolder(Feature feature) {
		FrozenLibLogUtils.log("Registering configured feature: " + this.getKey().identifier(), FrozenLibLogUtils.UNSTABLE_LOGGING);

		assert FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT != null : "Bootstrap context is null while registering " + this.getKey().identifier();

		assert feature != null : "Feature is null whilst registering " + this.getKey().identifier();

		FrozenLibFeatureUtil.BOOTSTRAP_CONTEXT.register((ResourceKey) this.getKey(), feature);
		return this;
	}

	public FrozenLibFeature makeAndSetHolder(FrozenHolder<Feature, Feature> feature) {
		return makeAndSetHolder(feature.get());
	}
}
