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

package net.frozenblock.lib.worldgen.structure.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class RandomPoolAliasApi {
	private static final Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> ALIAS_TO_TARGETS = new Object2ObjectOpenHashMap<>();

	public static void addTarget(ResourceLocation alias, ResourceLocation target, int weight) {
		List<Pair<ResourceLocation, Integer>> list = ALIAS_TO_TARGETS.getOrDefault(alias, null);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(Pair.of(target, weight));
		ALIAS_TO_TARGETS.put(alias, list);
	}

	public static List<Pair<ResourceLocation, Integer>> getAdditionalTargets(ResourceLocation alias) {
		return ALIAS_TO_TARGETS.getOrDefault(alias, ImmutableList.of());
	}

}
