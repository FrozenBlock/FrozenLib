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
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibEarlyConstants;
import net.frozenblock.lib.levelgen.feature.api.stateproviders.LeafLitterStateProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class FrozenLibTreeFeature {
	private final FrozenLibFeature feature;
	private final FrozenLibFeature featureWithLitter;
	private final List<TreeDecorator> litterDecorators;

	public FrozenLibTreeFeature(
		Identifier key,
		Block leafLitterBlock,
		int triesA, int radiusA, int heightA,
		int triesB, int radiusB, int heightB
	) {
		this.feature = new FrozenLibFeature(key);
		this.featureWithLitter = new FrozenLibFeature(key.withSuffix("_leaf_litter"));
		this.litterDecorators = FrozenLibEarlyConstants.IS_DATAGEN ? new ArrayList<>() : null;
		if (this.litterDecorators == null) return;
		this.litterDecorators.add(makeLeafLitterDecorator(leafLitterBlock, triesA, radiusA, heightA, 3));
		this.litterDecorators.add(makeLeafLitterDecorator(leafLitterBlock, triesB, radiusB, heightB, 4));
	}

	private static PlaceOnGroundDecorator makeLeafLitterDecorator(Block leafLitterBlock, int tries, int radius, int height, int maxSegments) {
		return new PlaceOnGroundDecorator(
			tries,
			radius,
			height,
			Holder.direct(new LeafLitterStateProvider(leafLitterBlock, maxSegments))
		);
	}

	public ResourceKey<Feature> getKey() {
		return this.feature.getKey();
	}

	public ResourceKey<Feature> getLitterVariantKey() {
		return this.featureWithLitter.getKey();
	}

	public Holder<Feature> getHolder() {
		return this.feature.getHolder();
	}

	public WeightedPlacedFeature asWeightedPlacedFeature(float weight, PlacementModifier... placementModifiers) {
		return this.feature.asWeightedPlacedFeature(weight, placementModifiers);
	}

	public WeightedPlacedFeature litterAsWeightedPlacedFeature(float weight, PlacementModifier... placementModifiers) {
		return this.featureWithLitter.asWeightedPlacedFeature(weight, placementModifiers);
	}

	public Holder<Feature> getLitterVariantHolder() {
		return this.featureWithLitter.getHolder();
	}

	public Holder<PlacedFeature> asInlinePlaced(PlacementModifier... placementModifiers) {
		return this.feature.asInlinePlaced(placementModifiers);
	}

	public Holder<PlacedFeature> litterAsInlinePlaced(PlacementModifier... placementModifiers) {
		return this.featureWithLitter.asInlinePlaced(placementModifiers);
	}

	public Feature getFeature(LevelReader level) {
		return this.feature.getFeature(level);
	}

	public Feature getLitterVariantFeature(LevelReader level) {
		return this.featureWithLitter.getFeature(level);
	}

	public FrozenLibPlacedTreeFeature toPlacedFeature() {
		return new FrozenLibPlacedTreeFeature(this);
	}

	public <F extends TreeFeature> FrozenLibTreeFeature makeAndSetHolders(F feature) {
		this.feature.makeAndSetHolder(feature);

		final List<TreeDecorator> decorators = new ArrayList<>(feature.decorators());
		decorators.addAll(this.litterDecorators);
		final TreeFeature featureWithLitter = new TreeFeature(
			feature.trunkProvider(),
			feature.trunkPlacer(),
			feature.foliageProvider(),
			feature.foliagePlacer(),
			feature.rootPlacer(),
			feature.minimumSize(),
			List.copyOf(decorators),
			feature.ignoreVines(),
			feature.belowTrunkProvider()
		);
		this.featureWithLitter.makeAndSetHolder(featureWithLitter);

		return this;
	}
}
