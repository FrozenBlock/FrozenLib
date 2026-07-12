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

package net.frozenblock.lib.advancement.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;

// TODO: NeoForge does not support widening access to record fields, and neither loader supports this via accessor mixins.
// The Advancement API needs to be completely rewritten so it *replaces* the entire advancement, instead of modifying it.
@UtilityClass
public final class AdvancementAPI {

	/**
	 * Makes a copy of {@link AdvancementRewards#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRewards(Advancement advancement) {
		if (advancement.frozenLib$getRewards() == AdvancementRewards.EMPTY) advancement.frozenLib$setRewards(new AdvancementRewards(0, List.of(), List.of(), Optional.empty()));
	}

	/**
	 * Makes a copy of {@link AdvancementRequirements#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRequirements(Advancement advancement) {
		if (advancement.frozenLib$getRequirements() == AdvancementRequirements.EMPTY) advancement.frozenLib$setRequirements(new AdvancementRequirements(List.of()));
	}

	public static void setupCriteria(Advancement advancement) {
		if (!(advancement.frozenLib$getCriteria() instanceof HashMap<String, Criterion<?>>)) advancement.frozenLib$setCriteria(new HashMap<>(advancement.frozenLib$getCriteria()));
	}

	public static void addCriteria(Advancement advancement, String key, Criterion<?> criterion) {
		if (criterion == null) return;
		setupCriteria(advancement);
		advancement.frozenLib$getCriteria().putIfAbsent(key, criterion);
	}

	public static void addRequirementsAsNewList(Advancement advancement, AdvancementRequirements requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);

		final List<List<String>> requirementsList = new ArrayList<>(advancement.frozenLib$getRequirementsInterface().frozenLib$getRequirements());
		requirementsList.addAll(requirements.frozenLib$getRequirements());
		advancement.frozenLib$getRequirementsInterface().frozenLib$setRequirements(Collections.unmodifiableList(requirementsList));
	}

	public static void addRequirementsToList(Advancement advancement, List<String> newRequirements) {
		if (newRequirements == null || newRequirements.isEmpty()) return;
		setupRequirements(advancement);

		final List<List<String>> requirementsList = new ArrayList<>(advancement.frozenLib$getRequirementsInterface().frozenLib$getRequirements());
		if (requirementsList.isEmpty()) {
			requirementsList.add(newRequirements);
		} else {
			final List<String> existingList = requirementsList.getFirst();
			final List<String> finalList = new ArrayList<>(existingList);
			finalList.addAll(newRequirements);
			requirementsList.add(Collections.unmodifiableList(finalList));
			requirementsList.remove(existingList);
		}
		advancement.frozenLib$getRequirementsInterface().frozenLib$setRequirements(Collections.unmodifiableList(requirementsList));
	}

	public static void addLootTables(Advancement advancement, List<ResourceKey<LootTable>> newLootTables) {
		if (newLootTables.isEmpty()) return;
		setupRewards(advancement);

		final List<ResourceKey<LootTable>> finalLootTables = new ArrayList<>(advancement.frozenLib$getRewards().frozenLib$getLoot());
		finalLootTables.addAll(newLootTables);
		advancement.frozenLib$getRewards().frozenLib$setLoot(Collections.unmodifiableList(finalLootTables));
	}

	public static void addRecipes(Advancement advancement, List<ResourceKey<Recipe<?>>> newRecipes) {
		if (newRecipes.isEmpty()) return;
		setupRewards(advancement);

		final List<ResourceKey<Recipe<?>>> finalRecipes = new ArrayList<>(advancement.frozenLib$getRewards().frozenLib$getRecipes());
		finalRecipes.addAll(newRecipes);
		advancement.frozenLib$getRewards().frozenLib$setRecipes(Collections.unmodifiableList(finalRecipes));
	}
}
