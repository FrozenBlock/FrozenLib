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

package net.frozenblock.lib.worldgen.structure.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;

@UtilityClass
public class RandomPoolAliasApi {
	private static final Map<Identifier, List<Pair<Identifier, Integer>>> ALIAS_TO_TARGETS = new Object2ObjectOpenHashMap<>();

	public static void addTarget(Identifier alias, Identifier target, int weight) {
		List<Pair<Identifier, Integer>> list = ALIAS_TO_TARGETS.getOrDefault(alias, new ArrayList<>());
		list.add(Pair.of(target, weight));
		ALIAS_TO_TARGETS.put(alias, list);
	}

	public static List<Pair<Identifier, Integer>> getAdditionalTargets(Identifier alias) {
		return ALIAS_TO_TARGETS.getOrDefault(alias, ImmutableList.of());
	}

}
