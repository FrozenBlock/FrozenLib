/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.advancement.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public final class AdvancementAPI {
	private AdvancementAPI() {}

	/**
	 * Makes a copy of {@link AdvancementRewards#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRewards(Advancement advancement) {
		if (advancement.rewards == AdvancementRewards.EMPTY) {
			advancement.rewards = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());
		}
	}

	/**
	 * Makes a copy of {@link AdvancementRequirements#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRequirements(Advancement advancement) {
		if (advancement.requirements == AdvancementRequirements.EMPTY) {
			advancement.requirements = new AdvancementRequirements(List.of());
		}
	}

	public static void setupCriteria(Advancement advancement) {
		if (!(advancement.criteria instanceof HashMap<String, Criterion<?>>)) {
			advancement.criteria = new HashMap<>(advancement.criteria);
		}
	}

	public static void addCriteria(Advancement advancement, String key, Criterion<?> criterion) {
		if (criterion == null) return;
		setupCriteria(advancement);
		advancement.criteria().putIfAbsent(key, criterion);
	}

	public static void addRequirementsAsNewList(Advancement advancement, AdvancementRequirements requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);
		List<List<String>> list = new ArrayList<>(advancement.requirements().requirements);
		list.addAll(requirements.requirements);
		advancement.requirements().requirements = Collections.unmodifiableList(list);
	}

	public static void addRequirementsToList(Advancement advancement, List<String> requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);
		List<List<String>> list = new ArrayList<>(advancement.requirements().requirements);
		if (list.isEmpty()) {
			list.add(requirements);
		} else {
			List<String> existingList = list.get(0);
			List<String> finalList = new ArrayList<>();
			finalList.addAll(existingList);
			finalList.addAll(requirements);
			list.add(Collections.unmodifiableList(finalList));
		}
		advancement.requirements().requirements = Collections.unmodifiableList(list);
	}

	public static void addLootTables(Advancement advancement, List<ResourceLocation> lootTables) {
		if (lootTables.isEmpty()) return;
		setupRewards(advancement);
		AdvancementRewards rewards = advancement.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(rewards.loot);
		newLoot.addAll(lootTables);
		rewards.loot = Collections.unmodifiableList(newLoot);
	}

	public static void addRecipes(Advancement advancement, List<ResourceLocation> recipes) {
		AdvancementRewards rewards = advancement.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(rewards.recipes);
		newLoot.addAll(recipes);
		rewards.recipes = Collections.unmodifiableList(newLoot);
	}
}
