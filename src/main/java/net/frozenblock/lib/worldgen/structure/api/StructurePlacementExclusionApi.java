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
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.frozenblock.lib.worldgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

@UtilityClass
public class StructurePlacementExclusionApi {
	private static final Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> STRUCTURE_SET_TO_EXCLUDED_STRUCTURE_SETS = new Object2ObjectOpenHashMap<>();

	public static void init() {
		ServerWorldEvents.LOAD.register((server, level) -> {
			level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).listElements().forEach(structureSetReference -> {
				if (structureSetReference.isBound() && (Object) (structureSetReference.value()) instanceof StructureSetAndPlacementInterface setAndPlacementInterface) {
					setAndPlacementInterface.frozenLib$addExclusions(
						getAdditionalExcludedStructureSets(structureSetReference.key().location()),
						level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET)
					);
				}
			});
		});
	}

	public static void addExclusion(ResourceLocation structureSet, ResourceLocation excludedFrom, int chunkCount) {
		List<Pair<ResourceLocation, Integer>> list = STRUCTURE_SET_TO_EXCLUDED_STRUCTURE_SETS.getOrDefault(structureSet, new ArrayList<>());
		list.add(Pair.of(excludedFrom, chunkCount));
		STRUCTURE_SET_TO_EXCLUDED_STRUCTURE_SETS.put(structureSet, list);
	}

	public static List<Pair<ResourceLocation, Integer>> getAdditionalExcludedStructureSets(ResourceLocation structureSet) {
		return STRUCTURE_SET_TO_EXCLUDED_STRUCTURE_SETS.getOrDefault(structureSet, ImmutableList.of());
	}
}
