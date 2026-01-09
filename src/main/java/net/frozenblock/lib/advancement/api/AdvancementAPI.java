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
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;

@UtilityClass
public final class AdvancementAPI {

	/**
	 * Makes a copy of {@link AdvancementRewards#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRewards(Advancement advancement) {
		if (advancement.rewards == AdvancementRewards.EMPTY) advancement.rewards = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());
	}

	/**
	 * Makes a copy of {@link AdvancementRequirements#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRequirements(Advancement advancement) {
		if (advancement.requirements == AdvancementRequirements.EMPTY) advancement.requirements = new AdvancementRequirements(List.of());
	}

	public static void setupCriteria(Advancement advancement) {
		if (!(advancement.criteria instanceof HashMap<String, Criterion<?>>)) advancement.criteria = new HashMap<>(advancement.criteria);
	}

	public static void addCriteria(Advancement advancement, String key, Criterion<?> criterion) {
		if (criterion == null) return;
		setupCriteria(advancement);
		advancement.criteria().putIfAbsent(key, criterion);
	}

	public static void addRequirementsAsNewList(Advancement advancement, AdvancementRequirements requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);

		final List<List<String>> requirementsList = new ArrayList<>(advancement.requirements().requirements);
		requirementsList.addAll(requirements.requirements);
		advancement.requirements().requirements = Collections.unmodifiableList(requirementsList);
	}

	public static void addRequirementsToList(Advancement advancement, List<String> newRequirements) {
		if (newRequirements == null || newRequirements.isEmpty()) return;
		setupRequirements(advancement);

		final List<List<String>> requirementsList = new ArrayList<>(advancement.requirements().requirements);
		if (requirementsList.isEmpty()) {
			requirementsList.add(newRequirements);
		} else {
			final List<String> existingList = requirementsList.getFirst();
			final List<String> finalList = new ArrayList<>(existingList);
			finalList.addAll(newRequirements);
			requirementsList.add(Collections.unmodifiableList(finalList));
			requirementsList.remove(existingList);
		}
		advancement.requirements().requirements = Collections.unmodifiableList(requirementsList);
	}

	public static void addLootTables(Advancement advancement, List<ResourceKey<LootTable>> newLootTables) {
		if (newLootTables.isEmpty()) return;
		setupRewards(advancement);

		final AdvancementRewards rewards = advancement.rewards();
		final List<ResourceKey<LootTable>> finalLootTables = new ArrayList<>(rewards.loot);
		finalLootTables.addAll(newLootTables);
		rewards.loot = Collections.unmodifiableList(finalLootTables);
	}

	public static void addRecipes(Advancement advancement, List<ResourceKey<Recipe<?>>> newRecipes) {
		if (newRecipes.isEmpty()) return;
		setupRewards(advancement);

		final AdvancementRewards rewards = advancement.rewards();
		final List<ResourceKey<Recipe<?>>> finalRecipes = new ArrayList<>(rewards.recipes);
		finalRecipes.addAll(newRecipes);
		rewards.recipes = Collections.unmodifiableList(finalRecipes);
	}
}
