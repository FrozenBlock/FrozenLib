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

package net.frozenblock.lib.levelgen.biome.api.modifications.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.frozenblock.lib.levelgen.feature.api.FrozenLibPlacedFeature;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class FeatureModification extends BiomeModification {
	private static final String FEATURE_REMOVALS_KEY = "removals";
	private static final String FEATURE_ADDITIONS_KEY = "additions";
	public static final MapCodec<FeatureModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(FeatureModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(FeatureModification::phase),
		Step.ListWrapper.CODEC.optionalFieldOf(FEATURE_REMOVALS_KEY, Step.ListWrapper.empty()).forGetter(modification -> modification.removals),
		Step.ListWrapper.CODEC.optionalFieldOf(FEATURE_ADDITIONS_KEY, Step.ListWrapper.empty()).forGetter(modification -> modification.additions)
	).apply(instance, FeatureModification::new));
	private final Step.ListWrapper removals;
	private final Step.ListWrapper additions;

	private FeatureModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		Step.ListWrapper removals,
		Step.ListWrapper additions
	) {
		super(selector, phase);
		this.removals = removals;
		this.additions = additions;
	}

	public static FeatureModification forRemoval(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<PlacedFeature> removals
	) {
		return new FeatureModification(selector, phase, Step.wrap(List.of(new Step(removals))), Step.ListWrapper.empty());
	}

	public static FeatureModification forAddition(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		GenerationStep.Decoration step,
		HolderSet<PlacedFeature> additions
	) {
		return new FeatureModification(selector, phase, Step.ListWrapper.empty(), Step.wrap(List.of(new Step(Optional.of(step), additions))));
	}

	public static FeatureModification forReplacement(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<PlacedFeature> removals,
		GenerationStep.Decoration step,
		HolderSet<PlacedFeature> additions
	) {
		return new FeatureModification(selector, phase, Step.wrap(List.of(new Step(removals))), Step.wrap(List.of(new Step(Optional.of(step), additions))));
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.GenerationSettingsContext generationSettings = context.getGenerationSettings();

		for (Step removalStep : this.removals) {
			if (removalStep.step.isPresent()) {
				for (Holder<PlacedFeature> feature : removalStep.features) generationSettings.removeFeature(removalStep.step.get(), feature.unwrapKey().orElseThrow());
			} else {
				for (Holder<PlacedFeature> feature : removalStep.features) generationSettings.removeFeature(feature.unwrapKey().orElseThrow());
			}
		}

		for (Step additionStep : this.additions) {
			final GenerationStep.Decoration step = additionStep.step.orElseThrow(
				() -> new IllegalStateException("Elements of " + FEATURE_ADDITIONS_KEY + " must always contain a generation step")
			);
			for (Holder<PlacedFeature> feature : additionStep.features) generationSettings.addFeature(step, feature.unwrapKey().orElseThrow());
		}
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.FEATURE.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);

		if (this.removals.isEmpty() && this.additions.isEmpty()) {
			context.reportProblem(new EmptyModificationProblem(true, FEATURE_REMOVALS_KEY, FEATURE_ADDITIONS_KEY));
			return;
		}

		if (!this.removals.isEmpty()) {
			final ValidationContext removalsContext = context.forField(FEATURE_REMOVALS_KEY);
			for (Step step : this.removals) step.validate(removalsContext);
		}

		if (!this.additions.isEmpty()) {
			final ValidationContext additionsContext = context.forField(FEATURE_ADDITIONS_KEY);

			boolean foundEmptyGenerationStep = false;
			for (Step step : this.additions) {
				if (step.step.isEmpty()) foundEmptyGenerationStep = true;
				step.validate(additionsContext);
			}

			if (foundEmptyGenerationStep) additionsContext.reportProblem(new EmptyGenerationStepInListProblem(FEATURE_ADDITIONS_KEY));
		}
	}

	public record Step(Optional<GenerationStep.Decoration> step, HolderSet<PlacedFeature> features) implements Validatable {
		public static final Codec<Step> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			GenerationStep.Decoration.CODEC.optionalFieldOf("step").forGetter(Step::step),
			RegistryCodecs.holderSet(Registries.PLACED_FEATURE).optionalFieldOf("features", HolderSet.empty()).forGetter(step -> step.features)
		).apply(instance, Step::new));

		private Step(HolderSet<PlacedFeature> features) {
			this(Optional.empty(), features);
		}

		private static ListWrapper wrap(List<Step> steps) {
			if (steps.size() == 1) return new ListWrapper(Either.left(steps.getFirst()));
			return new ListWrapper(Either.right(steps));
		}

		@Override
		public void validate(ValidationContext context) {
			if (this.features.size() <= 0) context.reportProblem(new ValidationContext.MinBoundsProblem(1));

			for (int i = 0; i < this.features.size(); i++) {
				validateFeatureHolder(context.forChild(new ProblemReporter.IndexedPathElement(i)), this.features.get(i).unwrapKey().orElseThrow());
			}
		}

		private static void validateFeatureHolder(ValidationContext context, ResourceKey<PlacedFeature> key) {
			if (context.hasVisitedElement(key)) {
				context.reportProblem(new ValidationContext.RecursiveElementReferenceProblem(key));
				return;
			}

			context.enterElement(new ProblemReporter.ElementReferencePathElement(key), key);
		}

		private record ListWrapper(Either<Step, List<Step>> wrapped) implements Iterable<Step> {
			private static final ListWrapper EMPTY = new ListWrapper(Either.right(List.of()));
			private static final Codec<ListWrapper> CODEC = Codec.either(Step.CODEC, Step.CODEC.listOf()).xmap(ListWrapper::new, ListWrapper::wrapped);

			private static ListWrapper empty() {
				return EMPTY;
			}

			public boolean isEmpty() {
				return this.wrapped.map(step -> false, List::isEmpty);
			}

			@Override
			public Iterator<Step> iterator() {
				return this.wrapped.map(List::of, steps -> steps).iterator();
			}
		}
	}

	// HELPER METHODS
	// REPLACEMENT
	public static void registerLocalReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.LOCAL_MODIFICATIONS, additions);
	}

	public static void registerLocalReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.LOCAL_MODIFICATIONS, additions);
	}

	public static void registerOreReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.UNDERGROUND_ORES, additions);
	}

	public static void registerOreReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.UNDERGROUND_ORES, additions);
	}

	public static void registerUndergroundReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.UNDERGROUND_DECORATION, additions);
	}

	public static void registerUndergroundReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.UNDERGROUND_DECORATION, additions);
	}

	public static void registerVegetalReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.VEGETAL_DECORATION, additions);
	}

	public static void registerVegetalReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.VEGETAL_DECORATION, additions);
	}

	public static void registerTopLayerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.TOP_LAYER_MODIFICATION, additions);
	}

	public static void registerTopLayerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		List<FrozenLibPlacedFeature> additions
	) {
		registerReplacement(context, biomes, primaryBiomeSelector, removals, GenerationStep.Decoration.TOP_LAYER_MODIFICATION, additions);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		GenerationStep.Decoration step,
		List<FrozenLibPlacedFeature> additions
	) {
		FeatureModification.registerReplacement(
			context,
			biomes,
			primaryBiomeSelector,
			removals,
			step,
			additions.toArray(new FrozenLibPlacedFeature[0])
		);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		GenerationStep.Decoration step,
		List<FrozenLibPlacedFeature> additions
	) {
		FeatureModification.registerReplacement(
			context,
			biomes,
			primaryBiomeSelector,
			removals,
			step,
			additions.toArray(new FrozenLibPlacedFeature[0])
		);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		HolderSet<PlacedFeature> removals,
		GenerationStep.Decoration step,
		HolderSet<PlacedFeature> additions
	) {
		context.register(
			createKey(biomes),
			forReplacement(
				Holder.direct(
					BiomeSelector.allOf(
						HolderSet.direct(
							context.lookup(FrozenLibRegistries.BIOME_SELECTOR).getOrThrow(primaryBiomeSelector),
							Holder.direct(BiomeSelector.inBiomeTag(context, biomes))
						)
					)
				),
				ModificationPhase.REPLACEMENTS,
				removals,
				step,
				additions
			)
		);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		GenerationStep.Decoration step,
		ResourceKey<PlacedFeature>... additions
	) {
		final HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
		registerReplacement(
			context,
			biomes,
			primaryBiomeSelector,
			HolderSet.direct(placedFeatures::getOrThrow, removals),
			step,
			HolderSet.direct(placedFeatures::getOrThrow, additions)
		);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		List<ResourceKey<PlacedFeature>> removals,
		GenerationStep.Decoration step,
		FrozenLibPlacedFeature... additions
	) {
		registerReplacement(
			context,
			biomes,
			primaryBiomeSelector,
			removals,
			step,
			Arrays.stream(additions).map(FrozenLibPlacedFeature::getKey).toArray(ResourceKey[]::new)
		);
	}

	public static void registerReplacement(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		ResourceKey<BiomeSelector> primaryBiomeSelector,
		TagKey<PlacedFeature> removals,
		GenerationStep.Decoration step,
		FrozenLibPlacedFeature... additions
	) {
		final HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
		registerReplacement(
			context,
			biomes,
			primaryBiomeSelector,
			placedFeatures.getOrThrow(removals),
			step,
			HolderSet.direct(Arrays.stream(additions).map(FrozenLibPlacedFeature::getHolder).toList())
		);
	}

	// ADDITION
	public static void registerLakeAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.LAKES, features);
	}

	public static void registerLocalAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.LOCAL_MODIFICATIONS, features);
	}

	public static void registerOreAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.UNDERGROUND_ORES, features);
	}

	public static void registerUndergroundAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.UNDERGROUND_DECORATION, features);
	}

	public static void registerFluidSpringAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.FLUID_SPRINGS, features);
	}

	public static void registerVegetalAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.VEGETAL_DECORATION, features);
	}

	public static void registerTopLayerAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		FrozenLibPlacedFeature... features
	) {
		registerAddition(context, biomes, GenerationStep.Decoration.TOP_LAYER_MODIFICATION, features);
	}

	public static void registerAddition(
		BootstrapContext<BiomeModification> context,
		TagKey<Biome> biomes,
		GenerationStep.Decoration step,
		FrozenLibPlacedFeature... features
	) {
		context.register(
			createKey(biomes),
			forAddition(
				Holder.direct(BiomeSelector.inBiomeTag(context, biomes)),
				ModificationPhase.ADDITIONS,
				step,
				HolderSet.direct(Arrays.stream(features).map(FrozenLibPlacedFeature::getHolder).toList())
			)
		);
	}
}
