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
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplatePoolInterface;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

@UtilityClass
public class TemplatePoolApi {
	private static final Supplier<Boolean> ALWAYS_TRUE = () -> true;
	private static final Map<ResourceLocation, List<Pair<Supplier<Boolean>, List<Pair<StructurePoolElement, Integer>>>>> POOL_TO_ELEMENTS = new Object2ObjectOpenHashMap<>();

	/**
	 * An event used to add new {@link StructurePoolElement}s to {@link StructureTemplatePool}s.
	 * <p>
	 * Use {@link TemplatePoolApi#addElement(ResourceLocation, Function, int, StructureTemplatePool.Projection)} or {@link TemplatePoolApi#addElement(Supplier, ResourceLocation, Function, int, StructureTemplatePool.Projection)} for this.
	 */
	public static final Event<AddAdditionalTemplatePools> ADD_ADDITIONAL_TEMPLATE_POOLS = FrozenEvents.createEnvironmentEvent(AddAdditionalTemplatePools.class,
		callbacks -> (registry) -> {
			for (var callback : callbacks) callback.addAdditionalTemplatePools(registry);
	});

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			POOL_TO_ELEMENTS.clear();

			registryAccess.lookup(Registries.PROCESSOR_LIST).ifPresent(processorListRegistry -> {
				ADD_ADDITIONAL_TEMPLATE_POOLS.invoker().addAdditionalTemplatePools(processorListRegistry);
			});

			registryAccess.lookup(Registries.TEMPLATE_POOL).ifPresent(templatePoolRegistry -> {
				templatePoolRegistry.entrySet().forEach(templatePoolEntry -> {
					if ((Object) (templatePoolEntry.getValue()) instanceof StructureTemplatePoolInterface templatePoolInterface) {
						final List<Pair<StructurePoolElement, Integer>> additionalElements = getAdditionalElements(templatePoolEntry.getKey().location());
						if (!additionalElements.isEmpty()) templatePoolInterface.frozenlib$addTemplatePools(additionalElements);
					}
				});
			});
		});

		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> POOL_TO_ELEMENTS.clear());
	}

	public static void addElement(
		ResourceLocation pool,
		@NotNull Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> elementFunction,
		int weight,
		StructureTemplatePool.Projection projection
	) {
		addElement(ALWAYS_TRUE, pool, elementFunction, weight, projection);
	}

	public static void addElement(
		Supplier<Boolean> condition,
		ResourceLocation pool,
		@NotNull Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> elementFunction,
		int weight,
		StructureTemplatePool.Projection projection
	) {
		final StructurePoolElement element = elementFunction.apply(projection);

		final List<Pair<Supplier<Boolean>, List<Pair<StructurePoolElement, Integer>>>> list = POOL_TO_ELEMENTS.getOrDefault(pool, new ArrayList<>());
		final List<Pair<StructurePoolElement, Integer>> elements = list
			.stream()
			.filter(pair -> pair.getFirst() == condition)
			.findFirst().orElse(Pair.of(condition, new ArrayList<>()))
			.getSecond();

		elements.add(Pair.of(element, weight));

		POOL_TO_ELEMENTS.put(pool, list);
	}

	public static @NotNull List<Pair<StructurePoolElement, Integer>> getAdditionalElements(ResourceLocation pool) {
		final ArrayList<Pair<StructurePoolElement, Integer>> elements = new ArrayList<>();
		POOL_TO_ELEMENTS.getOrDefault(pool, ImmutableList.of()).forEach(entry -> {
			if (entry.getFirst().get()) elements.addAll(entry.getSecond());
		});
		return elements;
	}

	@FunctionalInterface
	public interface AddAdditionalTemplatePools extends CommonEventEntrypoint {
		void addAdditionalTemplatePools(HolderLookup.RegistryLookup<StructureProcessorList> registry);
	}

}
