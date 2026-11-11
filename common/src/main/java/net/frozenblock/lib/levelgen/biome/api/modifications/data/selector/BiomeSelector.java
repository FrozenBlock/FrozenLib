/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.frozenblock.lib.registry.FrozenLibBuiltInRegistries;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.Validatable;

public interface BiomeSelector extends Validatable {
	Codec<BiomeSelector> DIRECT_CODEC = FrozenLibBuiltInRegistries.BIOME_SELECTOR_TYPE.byNameCodec().dispatch(BiomeSelector::codec, Function.identity());
	Codec<Holder<BiomeSelector>> HOLDER_CODEC = RegistryCodecs.holder(FrozenLibRegistries.BIOME_SELECTOR, DIRECT_CODEC);
	Codec<HolderSet<BiomeSelector>> HOLDER_SET_CODEC = RegistryCodecs.holderSet(FrozenLibRegistries.BIOME_SELECTOR, DIRECT_CODEC);

	boolean test(BiomeSelectionContext context);

	MapCodec<? extends BiomeSelector> codec();

	static BiomeSelector inDimension(ResourceKey<LevelStem> dimension) {
		return new InDimension(dimension);
	}

	static BiomeSelector inBiomes(HolderSet<Biome> biomes) {
		return new InBiomes(biomes);
	}

	static BiomeSelector inBiome(BootstrapContext<?> context, ResourceKey<Biome> biome) {
		return new InBiomes(HolderSet.direct(context.lookup(Registries.BIOME).getOrThrow(biome)));
	}

	static BiomeSelector inBiomeTag(BootstrapContext<?> context, TagKey<Biome> biomes) {
		return inBiomes(context.lookup(Registries.BIOME).getOrThrow(biomes));
	}

	static BiomeSelector containsFeature(ResourceKey<Feature> feature) {
		return new ContainsFeature(feature);
	}

	static BiomeSelector containsPlacedFeature(ResourceKey<PlacedFeature> placedFeature) {
		return new ContainsPlacedFeature(placedFeature);
	}

	static BiomeSelector allOf(HolderSet<BiomeSelector> selectors) {
		return new AllOfSelector(selectors);
	}

	static BiomeSelector allOf(List<BiomeSelector> selectors) {
		return allOf(HolderSet.direct(selectors.stream().map(Holder::direct).toList()));
	}

	static BiomeSelector allOf(BiomeSelector... selectors) {
		return allOf(Arrays.stream(selectors).toList());
	}

	static BiomeSelector anyOf(HolderSet<BiomeSelector> selectors) {
		return new AnyOfSelector(selectors);
	}

	static BiomeSelector anyOf(List<BiomeSelector> selectors) {
		return anyOf(HolderSet.direct(selectors.stream().map(Holder::direct).toList()));
	}

	static BiomeSelector anyOf(BiomeSelector... selectors) {
		return anyOf(Arrays.stream(selectors).toList());
	}

	static BiomeSelector not(Holder<BiomeSelector> selector) {
		return new InverseSelector(selector);
	}

	static BiomeSelector not(BiomeSelector selector) {
		return not(Holder.direct(selector));
	}

	static BiomeSelector conditional(Holder<BiomeSelector> condition, Holder<BiomeSelector> ifTrue, Holder<BiomeSelector> ifFalse) {
		return new ConditionalSelector(condition, ifTrue, ifFalse);
	}

	static BiomeSelector conditional(BiomeSelector condition, Holder<BiomeSelector> ifTrue, Holder<BiomeSelector> ifFalse) {
		return conditional(Holder.direct(condition), ifTrue, ifFalse);
	}

	static BiomeSelector conditional(Holder<BiomeSelector> condition, BiomeSelector ifTrue, BiomeSelector ifFalse) {
		return conditional(condition, Holder.direct(ifTrue), Holder.direct(ifFalse));
	}

	static BiomeSelector conditional(BiomeSelector condition, BiomeSelector ifTrue, BiomeSelector ifFalse) {
		return conditional(Holder.direct(condition), Holder.direct(ifTrue), Holder.direct(ifFalse));
	}

	static BiomeSelector configPredicate(Holder<ConfigPredicate> predicate) {
		return new ConfigSelector(predicate);
	}

	static BiomeSelector configPredicate(ConfigPredicate predicate) {
		return configPredicate(predicate.asHolder());
	}

	static BiomeSelector configPredicate(BootstrapContext<?> context, ResourceKey<ConfigPredicate> predicate) {
		return configPredicate(context.lookup(FrozenLibRegistries.CONFIG_PREDICATE).getOrThrow(predicate));
	}

	static BiomeSelector alwaysTrue() {
		return AlwaysTrueSelector.INSTANCE;
	}

	static BiomeSelector alwaysFalse() {
		return AlwaysFalseSelector.INSTANCE;
	}
}
