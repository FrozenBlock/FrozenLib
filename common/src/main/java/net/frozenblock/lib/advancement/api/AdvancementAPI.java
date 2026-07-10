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
import net.frozenblock.lib.advancement.impl.AdvancementInterface;
import net.frozenblock.lib.advancement.impl.AdvancementRequirementsInterface;
import net.frozenblock.lib.advancement.impl.AdvancementRewardsInterface;
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
		var inter = AdvancementInterface.class.cast(advancement);
		if (inter.frozenLib$getRewards() == AdvancementRewards.EMPTY) inter.frozenLib$setRewards(new AdvancementRewards(0, List.of(), List.of(), Optional.empty()));
	}

	/**
	 * Makes a copy of {@link AdvancementRequirements#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRequirements(Advancement advancement) {
		var inter = AdvancementInterface.class.cast(advancement);
		if (inter.frozenLib$getRequirements() == AdvancementRequirements.EMPTY) inter.frozenLib$setRequirements(new AdvancementRequirements(List.of()));
	}

	public static void setupCriteria(Advancement advancement) {
		var inter = AdvancementInterface.class.cast(advancement);
		if (!(inter.frozenLib$getCriteria() instanceof HashMap<String, Criterion<?>>)) inter.frozenLib$setCriteria(new HashMap<>(inter.frozenLib$getCriteria()));
	}

	public static void addCriteria(Advancement advancement, String key, Criterion<?> criterion) {
		if (criterion == null) return;
		setupCriteria(advancement);
		AdvancementInterface.class.cast(advancement).frozenLib$getCriteria().putIfAbsent(key, criterion);
	}

	public static void addRequirementsAsNewList(Advancement advancement, AdvancementRequirements requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);

		final AdvancementRequirementsInterface addedRequirements = AdvancementRequirementsInterface.class.cast(requirements);
		final AdvancementInterface inter = AdvancementInterface.class.cast(advancement);
		final List<List<String>> requirementsList = new ArrayList<>(inter.frozenLib$getRequirementsInterface().frozenLib$getRequirements());
		requirementsList.addAll(addedRequirements.frozenLib$getRequirements());
		inter.frozenLib$getRequirementsInterface().frozenLib$setRequirements(Collections.unmodifiableList(requirementsList));
	}

	public static void addRequirementsToList(Advancement advancement, List<String> newRequirements) {
		if (newRequirements == null || newRequirements.isEmpty()) return;
		setupRequirements(advancement);

		final AdvancementInterface inter = AdvancementInterface.class.cast(advancement);
		final List<List<String>> requirementsList = new ArrayList<>(inter.frozenLib$getRequirementsInterface().frozenLib$getRequirements());
		if (requirementsList.isEmpty()) {
			requirementsList.add(newRequirements);
		} else {
			final List<String> existingList = requirementsList.getFirst();
			final List<String> finalList = new ArrayList<>(existingList);
			finalList.addAll(newRequirements);
			requirementsList.add(Collections.unmodifiableList(finalList));
			requirementsList.remove(existingList);
		}
		inter.frozenLib$getRequirementsInterface().frozenLib$setRequirements(Collections.unmodifiableList(requirementsList));
	}

	public static void addLootTables(Advancement advancement, List<ResourceKey<LootTable>> newLootTables) {
		if (newLootTables.isEmpty()) return;
		setupRewards(advancement);

		final AdvancementRewardsInterface rewards = AdvancementRewardsInterface.class.cast(AdvancementInterface.class.cast(advancement).frozenLib$getRewards());
		final List<ResourceKey<LootTable>> finalLootTables = new ArrayList<>(rewards.frozenLib$getLoot());
		finalLootTables.addAll(newLootTables);
		rewards.frozenLib$setLoot(Collections.unmodifiableList(finalLootTables));
	}

	public static void addRecipes(Advancement advancement, List<ResourceKey<Recipe<?>>> newRecipes) {
		if (newRecipes.isEmpty()) return;
		setupRewards(advancement);

		final AdvancementRewardsInterface rewards = AdvancementRewardsInterface.class.cast(AdvancementInterface.class.cast(advancement).frozenLib$getRewards());
		final List<ResourceKey<Recipe<?>>> finalRecipes = new ArrayList<>(rewards.frozenLib$getRecipes());
		finalRecipes.addAll(newRecipes);
		rewards.frozenLib$setRecipes(Collections.unmodifiableList(finalRecipes));
	}
}
