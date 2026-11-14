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
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
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
		RegistryCodecs.holderSet(Registries.CARVER).optionalFieldOf(CARVER_REMOVALS_KEY, HolderSet.empty()).forGetter(modification -> modification.removals),
		RegistryCodecs.holderSet(Registries.CARVER).optionalFieldOf(CARVER_ADDITIONS_KEY, HolderSet.empty()).forGetter(modification -> modification.additions)
	).apply(instance, CarverModification::new));
	private final HolderSet<WorldCarver> removals;
	private final HolderSet<WorldCarver> additions;

	protected CarverModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<WorldCarver> removals,
		HolderSet<WorldCarver> additions
	) {
		super(selector, phase);
		this.removals = removals;
		this.additions = additions;
	}

	// HELPER METHODS
	protected static CarverModification forRemoval(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<WorldCarver> removals
	) {
		return new CarverModification(selector, phase, removals, HolderSet.empty());
	}

	protected static CarverModification forAddition(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<WorldCarver> additions
	) {
		return new CarverModification(selector, phase, HolderSet.empty(), additions);
	}

	protected static CarverModification forReplacement(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<WorldCarver> removals,
		HolderSet<WorldCarver> additions
	) {
		return new CarverModification(selector, phase, removals, additions);
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.GenerationSettingsContext generationSettings = context.getGenerationSettings();
		for (Holder<WorldCarver> removal : this.removals) generationSettings.removeCarver(removal.unwrapKey().orElseThrow());
		for (Holder<WorldCarver> addition : this.additions) generationSettings.addCarver(addition.unwrapKey().orElseThrow());
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.CARVER.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);

		if (this.removals.unwrapKey().isEmpty() && this.removals.size() <= 0 && this.additions.unwrapKey().isEmpty() && this.additions.size() <= 0) {
			context.reportProblem(new EmptyModificationProblem(true, CARVER_REMOVALS_KEY, CARVER_ADDITIONS_KEY));
			return;
		}

		if (this.removals.size() > 0) {
			final ValidationContext removalsContext = context.forField(CARVER_REMOVALS_KEY);
			validateCarverHolderSet(removalsContext, this.removals);
		}

		if (this.additions.size() > 0) {
			final ValidationContext additionsContext = context.forField(CARVER_ADDITIONS_KEY);
			validateCarverHolderSet(additionsContext, this.additions);
		}
	}

	private static void validateCarverHolderSet(ValidationContext context, HolderSet<WorldCarver> carvers) {
		for (int i = 0; i < carvers.size(); i++) {
			validateCarverKey(context.forChild(new ProblemReporter.IndexedPathElement(i)), carvers.get(i).unwrapKey().orElseThrow());
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
