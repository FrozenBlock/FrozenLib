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
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TemplatePoolApi {
	private static final Supplier<Boolean> ALWAYS_TRUE = () -> true;
	private static final Map<ResourceLocation, List<Pair<Supplier<Boolean>, List<Pair<StructurePoolElement, Integer>>>>> POOL_TO_ELEMENTS = new Object2ObjectOpenHashMap<>();

	public static void addElement(
		ResourceLocation pool,
		@NotNull Function<StructureTemplatePool.Projection, StructurePoolElement> elementFunction,
		int weight,
		StructureTemplatePool.Projection projection
	) {
		addElement(ALWAYS_TRUE, pool, elementFunction, weight, projection);
	}

	public static void addElement(
		Supplier<Boolean> condition,
		ResourceLocation pool,
		@NotNull Function<StructureTemplatePool.Projection, StructurePoolElement> elementFunction,
		int weight,
		StructureTemplatePool.Projection projection
	) {
		final StructurePoolElement element = elementFunction.apply(projection);

		final List<Pair<Supplier<Boolean>, List<Pair<StructurePoolElement, Integer>>>> list = POOL_TO_ELEMENTS.getOrDefault(pool, new ArrayList<>());
		final List<Pair<StructurePoolElement, Integer>> elements = list
			.stream()
			.filter(pair -> pair.getFirst() == condition)
			.findFirst().orElse(new Pair<>(condition, new ArrayList<>()))
			.getSecond();

		elements.add(new Pair<>(element, weight));

		POOL_TO_ELEMENTS.put(pool, list);
	}

	public static @NotNull List<Pair<StructurePoolElement, Integer>> getAdditionalElements(ResourceLocation pool) {
		final ArrayList<Pair<StructurePoolElement, Integer>> elements = new ArrayList<>();
		POOL_TO_ELEMENTS.getOrDefault(pool, ImmutableList.of()).forEach(entry -> {
			if (entry.getFirst().get()) elements.addAll(entry.getSecond());
		});
		return elements;
	}

}
