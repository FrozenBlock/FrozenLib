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

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.PlaceOnGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class FrozenLibConfiguredTreeFeature {
	private final FrozenLibConfiguredFeature<TreeConfiguration> feature;
	private final FrozenLibConfiguredFeature<TreeConfiguration> featureWithLitter;
	private final List<TreeDecorator> litterDecorators = new ArrayList<>();

	public FrozenLibConfiguredTreeFeature(
		ResourceLocation key,
		Block leafLitterBlock,
		int triesA, int radiusA, int heightA,
		int triesB, int radiusB, int heightB
	) {
		this.feature = new FrozenLibConfiguredFeature<>(key);
		this.featureWithLitter = new FrozenLibConfiguredFeature<>(key.withSuffix("_leaf_litter"));
		this.litterDecorators.add(makeLeafLitterDecorator(leafLitterBlock, triesA, radiusA, heightA, 3));
		this.litterDecorators.add(makeLeafLitterDecorator(leafLitterBlock, triesB, radiusB, heightB, 4));
	}

	@Contract("_, _, _, _, _ -> new")
	private static @NotNull PlaceOnGroundDecorator makeLeafLitterDecorator(Block leafLitterBlock, int tries, int radius, int height, int maxSegments) {
		return new PlaceOnGroundDecorator(
			tries,
			radius,
			height,
			new WeightedStateProvider(
				VegetationFeatures.segmentedBlockPatchBuilder(
					leafLitterBlock,
					1,
					maxSegments,
					LeafLitterBlock.AMOUNT,
					LeafLitterBlock.FACING
				)
			)
		);
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getKey() {
		return this.feature.getKey();
	}

	public ResourceKey<ConfiguredFeature<?, ?>> getLitterVariantKey() {
		return this.featureWithLitter.getKey();
	}

	public Holder<ConfiguredFeature<?, ?>> getHolder() {
		return this.feature.getHolder();
	}

	public Holder<ConfiguredFeature<?, ?>> getLitterVariantHolder() {
		return this.featureWithLitter.getHolder();
	}

	public ConfiguredFeature<?, ?> getConfiguredFeature(LevelReader level) {
		return this.feature.getConfiguredFeature(level);
	}

	public ConfiguredFeature<?, ?> getLitterVariantConfiguredFeature(LevelReader level) {
		return this.featureWithLitter.getConfiguredFeature(level);
	}

	public FrozenLibPlacedTreeFeature toPlacedFeature() {
		return new FrozenLibPlacedTreeFeature(this);
	}

	public <F extends Feature<TreeConfiguration>> FrozenLibConfiguredTreeFeature makeAndSetHolders(F feature, TreeConfiguration config) {
		this.feature.makeAndSetHolder(feature, config);

		List<TreeDecorator> decorators = new ArrayList<>(config.decorators);
		decorators.addAll(this.litterDecorators);
		TreeConfiguration withLitterConfig = new TreeConfiguration(
			config.trunkProvider,
			config.trunkPlacer,
			config.foliageProvider,
			config.foliagePlacer,
			config.rootPlacer,
			config.dirtProvider,
			config.minimumSize,
			List.copyOf(decorators),
			config.ignoreVines,
			config.forceDirt
		);
		this.featureWithLitter.makeAndSetHolder(feature, withLitterConfig);

		return this;
	}
}
