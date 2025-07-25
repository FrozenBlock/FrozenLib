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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.worldgen.structure.impl.StructureSetAndPlacementInterface;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class StructureGenerationConditionApi {
	private static final Map<ResourceLocation, List<Supplier<Boolean>>> STRUCTURE_SET_TO_SUPPLIER_MAP = new Object2ObjectOpenHashMap<>();

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register((RegistryAccess registryAccess) -> {
			registryAccess.registry(Registries.STRUCTURE_SET).ifPresent(structureSetRegistry -> {
				structureSetRegistry.entrySet().forEach(structureSetEntry -> {
					if ((Object) (structureSetEntry.getValue()) instanceof StructureSetAndPlacementInterface setAndPlacementInterface) {
						setAndPlacementInterface.frozenLib$addGenerationConditions(getGenerationConditions(structureSetEntry.getKey().location()));
					}
				});
			});
		});
	}

	public static void addGenerationCondition(ResourceLocation structureSet, Supplier<Boolean> generationCondition) {
		List<Supplier<Boolean>> list = STRUCTURE_SET_TO_SUPPLIER_MAP.getOrDefault(structureSet, new ArrayList<>());
		list.add(generationCondition);
		STRUCTURE_SET_TO_SUPPLIER_MAP.put(structureSet, list);
	}

	public static List<Supplier<Boolean>> getGenerationConditions(ResourceLocation structureSet) {
		return STRUCTURE_SET_TO_SUPPLIER_MAP.getOrDefault(structureSet, ImmutableList.of());
	}
}
