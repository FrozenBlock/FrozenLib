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

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenLibPlacedTreeFeature {
	private final FrozenLibConfiguredTreeFeature treeFeature;
	private final FrozenLibPlacedFeature placedFeature;
	private final FrozenLibPlacedFeature placedFeatureWithLitter;

	public FrozenLibPlacedTreeFeature(FrozenLibConfiguredTreeFeature treeFeature) {
		this.treeFeature = treeFeature;
		this.placedFeature = new FrozenLibPlacedFeature(this.treeFeature.getKey().identifier().withSuffix("_checked"));
		this.placedFeatureWithLitter = new FrozenLibPlacedFeature(this.treeFeature.getLitterVariantKey().identifier().withSuffix("_checked"));
	}

	public Holder<ConfiguredFeature<?, ?>> getConfiguredHolder() {
		return this.placedFeature.getConfiguredHolder();
	}

	public Holder<ConfiguredFeature<?, ?>> getLitterVariantConfiguredHolder() {
		return this.placedFeatureWithLitter.getConfiguredHolder();
	}

	public Holder<PlacedFeature> getHolder() {
		return this.placedFeature.getHolder();
	}

	public Holder<PlacedFeature> getLitterVariantHolder() {
		return this.placedFeatureWithLitter.getHolder();
	}

	public WeightedPlacedFeature asWeightedPlacedFeature(float chance) {
		return new WeightedPlacedFeature(this.getHolder(), chance);
	}

	public WeightedPlacedFeature litterAsWeightedPlacedFeature(float chance) {
		return new WeightedPlacedFeature(this.getLitterVariantHolder(), chance);
	}

	public FrozenLibPlacedTreeFeature makeAndSetHolders(List<PlacementModifier> modifiers) {
		this.placedFeature.makeAndSetHolder(this.treeFeature.getHolder(), modifiers);
		this.placedFeatureWithLitter.makeAndSetHolder(this.treeFeature.getLitterVariantHolder(), modifiers);
		return this;
	}

	public FrozenLibPlacedTreeFeature makeAndSetHolders(PlacementModifier... modifiers) {
		return this.makeAndSetHolders(List.of(modifiers));
	}
}
