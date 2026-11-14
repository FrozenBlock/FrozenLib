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
import java.util.Optional;
import net.frozenblock.lib.levelgen.biome.api.modifications.BiomeModificationContext;
import net.frozenblock.lib.levelgen.biome.api.modifications.ModificationPhase;
import net.frozenblock.lib.levelgen.biome.api.modifications.data.selector.BiomeSelector;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class MobSpawnModification extends BiomeModification {
	private static final String CREATURE_GENERATION_PROBABILITY_KEY = "creature_world_gen_spawn_probability";
	private static final String SPAWN_REMOVALS_KEY = "removals";
	private static final String SPAWN_ADDITIONS_KEY = "additions";
	public static final MapCodec<MobSpawnModification> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SELECTOR_MAP_CODEC.forGetter(MobSpawnModification::selector),
		MODIFICATION_PHASE_MAP_CODEC.forGetter(MobSpawnModification::phase),
		Codec.floatRange(0F, 0.9999999F).optionalFieldOf(CREATURE_GENERATION_PROBABILITY_KEY).forGetter(modification -> modification.creatureGenerationProbability),
		RegistryCodecs.holderSet(Registries.ENTITY_TYPE).optionalFieldOf(SPAWN_REMOVALS_KEY, HolderSet.empty()).forGetter(modification -> modification.removals),
		MobSpawnSettings.CODEC.optionalFieldOf(SPAWN_ADDITIONS_KEY, MobSpawnSettings.EMPTY).forGetter(modification -> modification.additions)
	).apply(instance, MobSpawnModification::new));
	private final Optional<Float> creatureGenerationProbability;
	private final HolderSet<EntityType<?>> removals;
	private final MobSpawnSettings additions;

	private MobSpawnModification(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		Optional<Float> creatureGenerationProbability,
		HolderSet<EntityType<?>> removals,
		MobSpawnSettings additions
	) {
		super(selector, phase);
		this.creatureGenerationProbability = creatureGenerationProbability;
		this.removals = removals;
		this.additions = additions;
	}

	public static MobSpawnModification forRemoval(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<EntityType<?>> removals
	) {
		return new MobSpawnModification(selector, phase, Optional.empty(), removals, MobSpawnSettings.EMPTY);
	}

	public static MobSpawnModification forAddition(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		MobSpawnSettings additions
	) {
		return new MobSpawnModification(selector, phase, Optional.empty(), HolderSet.empty(), additions);
	}

	public static MobSpawnModification forReplacement(
		Holder<BiomeSelector> selector,
		ModificationPhase phase,
		HolderSet<EntityType<?>> removals,
		MobSpawnSettings additions
	) {
		return new MobSpawnModification(selector, phase, Optional.empty(), removals, additions);
	}

	@Override
	public void modify(BiomeModificationContext context) {
		final BiomeModificationContext.MobSpawnSettingsContext spawnContext = context.getMobSpawnSettings();

		this.creatureGenerationProbability.ifPresent(spawnContext::setCreatureGenerationProbability);

		for (Holder<EntityType<?>> removal : this.removals) spawnContext.removeSpawnsOfEntityType(removal.value());

		for (MobCategory category : MobCategory.values()) {
			for (Weighted<MobSpawnSettings.SpawnerData> spawnerData : this.additions.getMobsToSpawn(category).unwrap()) {
				spawnContext.addSpawn(category, spawnerData.value(), spawnerData.weight());
			}
		}

		this.additions.allSpawnCosts().forEach((entityType, spawnCost) -> {
			spawnContext.addMobCharge(entityType, spawnCost.charge(),  spawnCost.energyBudget());
		});
	}

	@Override
	BiomeModificationType<? extends BiomeModification> type() {
		return BiomeModificationTypes.MOB_SPAWN.get();
	}

	@Override
	public void validate(ValidationContext context) {
		super.validate(context);

		if (this.creatureGenerationProbability.isEmpty()
			&& this.removals.unwrapKey().isPresent() && this.removals.size() <= 0
			&& this.additions.equals(MobSpawnSettings.EMPTY)
		) {
			context.reportProblem(new EmptyModificationProblem(true, CREATURE_GENERATION_PROBABILITY_KEY, SPAWN_REMOVALS_KEY, SPAWN_ADDITIONS_KEY));
			return;
		}

		if (this.removals.size() > 0) {
			final ValidationContext removalsContext = context.forField(SPAWN_REMOVALS_KEY);
			validateEntityTypeHolderSet(removalsContext, this.removals);
		}
	}

	private static void validateEntityTypeHolderSet(ValidationContext context, HolderSet<EntityType<?>> entityTypes) {
		for (int i = 0; i < entityTypes.size(); i++) {
			validateEntityTypeKey(context.forChild(new ProblemReporter.IndexedPathElement(i)), entityTypes.get(i).unwrapKey().orElseThrow());
		}
	}

	private static void validateEntityTypeKey(ValidationContext context, ResourceKey<EntityType<?>> key) {
		if (context.hasVisitedElement(key)) {
			context.reportProblem(new ValidationContext.RecursiveElementReferenceProblem(key));
			return;
		}

		context.enterElement(new ProblemReporter.ElementReferencePathElement(key), key);
	}
}
