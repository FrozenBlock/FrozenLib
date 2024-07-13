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
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureProcessorApi {
	private static final List<StructureProcessor> EMPTY = ImmutableList.of();

	private static final Map<ResourceLocation, List<StructureProcessor>> LOCATION_TO_PROCESSORS = new Object2ObjectOpenHashMap<>();
	private static final Map<String, List<StructureProcessor>> NAMESPACE_TO_PROCESSORS = new Object2ObjectOpenHashMap<>();
	private static final Map<String, List<StructureProcessor>> KEY_WORD_TO_PROCESSORS = new Object2ObjectOpenHashMap<>();
	private static final Map<Pair<String, String>, List<StructureProcessor>> NAMESPACE_AND_KEY_WORD_TO_PROCESSORS = new Object2ObjectOpenHashMap<>();

	public static void addTarget(ResourceLocation target, StructureProcessor processor) {
		List<StructureProcessor> processorList = LOCATION_TO_PROCESSORS.getOrDefault(target, null);
		if (processorList == null) {
			processorList = new ArrayList<>();
		}
		processorList.add(processor);
		LOCATION_TO_PROCESSORS.put(target, processorList);
	}

	public static void addNamespaceTarget(String namespace, StructureProcessor processor) {
		List<StructureProcessor> processorList = NAMESPACE_TO_PROCESSORS.getOrDefault(namespace, null);
		if (processorList == null) {
			processorList = new ArrayList<>();
		}
		processorList.add(processor);
		NAMESPACE_TO_PROCESSORS.put(namespace, processorList);
	}

	public static void addKeywordTarget(String keyWord, StructureProcessor processor) {
		List<StructureProcessor> processorList = KEY_WORD_TO_PROCESSORS.getOrDefault(keyWord, null);
		if (processorList == null) {
			processorList = new ArrayList<>();
		}
		processorList.add(processor);
		KEY_WORD_TO_PROCESSORS.put(keyWord, processorList);
	}

	public static void addNamespaceWithKeywordTarget(String namespace, String keyWord, StructureProcessor processor) {
		Pair<String, String> pair = Pair.of(namespace, keyWord);
		List<StructureProcessor> processorList = NAMESPACE_AND_KEY_WORD_TO_PROCESSORS.getOrDefault(pair, null);
		if (processorList == null) {
			processorList = new ArrayList<>();
		}
		processorList.add(processor);
		NAMESPACE_AND_KEY_WORD_TO_PROCESSORS.put(pair, processorList);
	}

	public static @NotNull List<StructureProcessor> getAdditionalProcessors(ResourceLocation location) {
		if (location == null) {
			return EMPTY;
		}
		String namespace = location.getNamespace();
		String path = location.getPath();

		List<StructureProcessor> locationToProcessors = LOCATION_TO_PROCESSORS.getOrDefault(location, EMPTY);
		List<StructureProcessor> processorList = new ArrayList<>(locationToProcessors);

		List<StructureProcessor> nameSpaceToProcessors = NAMESPACE_TO_PROCESSORS.getOrDefault(namespace, EMPTY);
		processorList.addAll(nameSpaceToProcessors);

		KEY_WORD_TO_PROCESSORS.forEach((s, structureProcessors) -> {
			if (path.contains(s)) {
				processorList.addAll(structureProcessors);
			}
		});

		NAMESPACE_AND_KEY_WORD_TO_PROCESSORS.forEach((pair, structureProcessors) -> {
			if (pair.getFirst().equals(namespace) && path.contains(pair.getSecond())) {
				processorList.addAll(structureProcessors);
			}
		});

		return processorList;
	}

}
