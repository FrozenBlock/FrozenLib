/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.advancement.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;

public final class AdvancementAPI {
	private AdvancementAPI() {}

	/**
	 * Makes a copy of {@link AdvancementRewards#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRewards(Advancement advancement) {
		if (advancement.rewards == AdvancementRewards.EMPTY) {
			advancement.rewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
		}
	}

	/**
	 * Makes a copy of {@link AdvancementRequirements#EMPTY} for use in the Advancement API
	 * <p>
	 * Use only when needed, as this will increase memory usage
	 */
	public static void setupRequirements(Advancement advancement) {
		if (advancement.requirements == AdvancementRequirements.EMPTY) {
			advancement.requirements = new AdvancementRequirements(new String[0][]);
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

	public static void addRequirements(Advancement advancement, AdvancementRequirements requirements) {
		if (requirements == null || requirements.isEmpty()) return;
		setupRequirements(advancement);
		List<String[]> list = new ArrayList<>();
		list.addAll(Arrays.stream(advancement.requirements().requirements).toList());
		list.addAll(Arrays.stream(requirements.requirements).toList());
		advancement.requirements().requirements = list.toArray(new String[][]{});
	}

	public static void addLootTables(Advancement advancement, List<ResourceLocation> lootTables) {
		if (lootTables.isEmpty()) return;
		setupRewards(advancement);
		AdvancementRewards rewards = advancement.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(Arrays.stream(rewards.loot).toList());
		newLoot.addAll(lootTables);
		rewards.loot = newLoot.toArray(new ResourceLocation[]{});
	}

	public static void addRecipes(Advancement advancement, List<ResourceLocation> recipes) {
		AdvancementRewards rewards = advancement.rewards();
		List<ResourceLocation> newLoot = new ArrayList<>(Arrays.stream(rewards.recipes).toList());
		newLoot.addAll(recipes);
		rewards.recipes = newLoot.toArray(new ResourceLocation[]{});
	}
}
