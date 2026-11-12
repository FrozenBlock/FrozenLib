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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class CarverModification extends BiomeModification {
	private static final String CARVER_REMOVALS_KEY = "removals";
	private static final String CARVER_ADDITIONS_KEY = "additions";
	public static final MapCodec<CarverModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(CarverModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(CarverModification::phase),
		ResourceKey.codec(Registries.CARVER).listOf().optionalFieldOf(CARVER_REMOVALS_KEY, List.of()).forGetter(modification -> modification.removals),
		ResourceKey.codec(Registries.CARVER).listOf().optionalFieldOf(CARVER_ADDITIONS_KEY, List.of()).forGetter(modification -> modification.additions)
	).apply(instance, CarverModification::new));
	private final List<ResourceKey<WorldCarver>> removals;
	private final List<ResourceKey<WorldCarver>> additions;

	protected CarverModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<WorldCarver>> removals,
		List<ResourceKey<WorldCarver>> additions
	) {
		super(selector, phase);
		this.removals = removals;
		this.additions = additions;
	}

	protected static CarverModification forRemovals(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<WorldCarver>> removals
	) {
		return new CarverModification(selector, phase, removals, List.of());
	}

	protected static CarverModification forAdditions(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<WorldCarver>> additions
	) {
		return new CarverModification(selector, phase, List.of(), additions);
	}

	protected static CarverModification forRemovalsAndAdditions(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		List<ResourceKey<WorldCarver>> removals,
		List<ResourceKey<WorldCarver>> additions
	) {
		return new CarverModification(selector, phase, removals, additions);
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.GenerationSettingsContext generationSettings = context.getGenerationSettings();
		for (ResourceKey<WorldCarver> removal : this.removals) generationSettings.removeCarver(removal);
		for (ResourceKey<WorldCarver> addition : this.additions) generationSettings.addCarver(addition);
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.CARVER.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);

		if (this.removals.isEmpty() && this.additions.isEmpty()) {
			context.reportProblem(new EmptyModificationProblem(true, CARVER_REMOVALS_KEY, CARVER_ADDITIONS_KEY));
			return;
		}

		if (!this.removals.isEmpty()) {
			final ValidationContext removalsContext = context.forField(CARVER_REMOVALS_KEY);
			validateCarverList(removalsContext, this.removals);
		}

		if (!this.additions.isEmpty()) {
			final ValidationContext additionsContext = context.forField(CARVER_ADDITIONS_KEY);
			validateCarverList(additionsContext, this.additions);
		}
	}

	private static void validateCarverList(ValidationContext context, List<ResourceKey<WorldCarver>> carvers) {
		for (int i = 0; i < carvers.size(); i++) {
			validateCarverKey(context.forChild(new ProblemReporter.IndexedPathElement(i)), carvers.get(i));
		}
	}

	private static void validateCarverKey(ValidationContext context, ResourceKey<WorldCarver> key) {
		if (context.hasVisitedElement(key)) {
			context.reportProblem(new ValidationContext.RecursiveElementReferenceProblem(key));
			return;
		}

		context.enterElement(new ProblemReporter.ElementReferencePathElement(key), key);
	}
}
