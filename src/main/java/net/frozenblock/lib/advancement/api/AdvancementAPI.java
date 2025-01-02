/*
 * Copyright (C) 2024-2025 FrozenBlock
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
			List<String> existingList = list.getFirst();
			List<String> finalList = new ArrayList<>(existingList);
			finalList.addAll(requirements);
			list.add(Collections.unmodifiableList(finalList));
			list.remove(existingList);
		}
		advancement.requirements().requirements = Collections.unmodifiableList(list);
	}

	public static void addLootTables(Advancement advancement, List<ResourceKey<LootTable>> lootTables) {
		if (lootTables.isEmpty()) return;
		setupRewards(advancement);
		AdvancementRewards rewards = advancement.rewards();
		List<ResourceKey<LootTable>> newLoot = new ArrayList<>(rewards.loot);
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
