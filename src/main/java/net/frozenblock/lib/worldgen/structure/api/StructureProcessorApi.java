/*
 * Copyright (C) 2024 FrozenBlock
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.jetbrains.annotations.NotNull;

public class StructureProcessorApi {
	private static final List<StructureProcessor> EMPTY = ImmutableList.of();

	private static final Map<ResourceLocation, List<StructureProcessor>> STRUCTURE_TO_PROCESSORS = new Object2ObjectOpenHashMap<>();

	public static void addProcessor(ResourceLocation structureId, StructureProcessor processor) {
		List<StructureProcessor> processorList = STRUCTURE_TO_PROCESSORS.getOrDefault(structureId, new ArrayList<>());
		processorList.add(processor);
		STRUCTURE_TO_PROCESSORS.put(structureId, processorList);
	}

	public static @NotNull List<StructureProcessor> getAdditionalProcessors(ResourceLocation structureId) {
		List<StructureProcessor> locationToProcessors = STRUCTURE_TO_PROCESSORS.getOrDefault(structureId, EMPTY);
		return new ArrayList<>(locationToProcessors);
	}

}
