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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import java.util.List;
import java.util.Optional;

public class FeatureModification extends BiomeModification {
	private static final String FEATURE_REMOVALS_KEY = "removals";
	private static final String FEATURE_ADDITIONS_KEY = "additions";
	public static final MapCodec<FeatureModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(FeatureModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(FeatureModification::phase),
		Step.CODEC.listOf().optionalFieldOf(FEATURE_REMOVALS_KEY, List.of()).forGetter(modification -> modification.removals),
		Step.CODEC.listOf().optionalFieldOf(FEATURE_ADDITIONS_KEY, List.of()).forGetter(modification -> modification.additions)
	).apply(instance, FeatureModification::new));
	private final List<Step> removals;
	private final List<Step> additions;

	protected FeatureModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<Step> removals,
		List<Step> additions
	) {
		super(selector, phase);
		this.removals = removals;
		this.additions = additions;
	}

	protected static FeatureModification forRemovals(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<PlacedFeature>> removals
	) {
		return new FeatureModification(selector, phase, List.of(new Step(removals)), List.of());
	}

	protected static FeatureModification forAdditions(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<PlacedFeature>> additions
	) {
		return new FeatureModification(selector, phase, List.of(), List.of(new Step(additions)));
	}

	protected static FeatureModification forRemovalsAndAdditions(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<PlacedFeature>> removals,
		GenerationStep.Decoration step,
		List<ResourceKey<PlacedFeature>> additions
	) {
		return new FeatureModification(selector, phase, List.of(new Step(removals)), List.of(new Step(Optional.of(step), additions)));
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.GenerationSettingsContext generationSettings = context.getGenerationSettings();

		for (Step removalStep : this.removals) {
			if (removalStep.step.isPresent()) {
				for (ResourceKey<PlacedFeature> feature : removalStep.features) generationSettings.removeFeature(removalStep.step.get(), feature);
			} else {
				for (ResourceKey<PlacedFeature> feature : removalStep.features) generationSettings.removeFeature(feature);
			}
		}

		for (Step additionStep : this.additions) {
			final GenerationStep.Decoration step = additionStep.step.orElseThrow(
				() -> new IllegalStateException("Elements of " + FEATURE_ADDITIONS_KEY + " must always contain a generation step")
			);
			for (ResourceKey<PlacedFeature> feature : additionStep.features) generationSettings.addFeature(step, feature);
		}
	}

	@Override
	MapCodec<? extends BiomeModification> codec() {
		return CODEC;
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

	public record Step(Optional<GenerationStep.Decoration> step, List<ResourceKey<PlacedFeature>> features) implements Validatable {
		public static final Codec<Step> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			GenerationStep.Decoration.CODEC.optionalFieldOf("step").forGetter(Step::step),
			ResourceKey.codec(Registries.PLACED_FEATURE).listOf().optionalFieldOf("features", List.of()).forGetter(step -> step.features)
		).apply(instance, Step::new));

		private Step(List<ResourceKey<PlacedFeature>> features) {
			this(Optional.empty(), features);
		}

		@Override
		public void validate(ValidationContext context) {
			if (this.features.isEmpty()) context.reportProblem(new ValidationContext.MinBoundsProblem(1));

			for (int i = 0; i < this.features.size(); i++) {
				validateFeatureKey(context.forChild(new ProblemReporter.IndexedPathElement(i)), this.features.get(i));
			}
		}

		private static void validateFeatureKey(ValidationContext context, ResourceKey<PlacedFeature> key) {
			if (context.hasVisitedElement(key)) {
				context.reportProblem(new ValidationContext.RecursiveElementReferenceProblem(key));
				return;
			}

			context.enterElement(new ProblemReporter.ElementReferencePathElement(key), key);
		}
	}
}
