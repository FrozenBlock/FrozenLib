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
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.frozenblock.lib.worldgen.structure.impl.StructureTemplatePoolInterface;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

@UtilityClass
public class TemplatePoolApi {
	/**
	 * An event used to add new {@link StructurePoolElement}s to {@link StructureTemplatePool}s.
	 */
	public static final Event<AddAdditionalTemplatePools> ADD_ADDITIONAL_TEMPLATE_POOLS = FrozenEvents.createEnvironmentEvent(AddAdditionalTemplatePools.class,
		callbacks -> (registry, context) -> {
			for (var callback : callbacks) callback.addAdditionalTemplatePools(registry, context);
	});

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			final TemplatePoolAdditionHolder context = new TemplatePoolAdditionHolder();

			registryAccess.lookup(Registries.PROCESSOR_LIST).ifPresent(processorListRegistry -> {
				ADD_ADDITIONAL_TEMPLATE_POOLS.invoker().addAdditionalTemplatePools(processorListRegistry, context);
			});

			registryAccess.lookup(Registries.TEMPLATE_POOL).ifPresent(templatePoolRegistry -> {
				templatePoolRegistry.entrySet().forEach(templatePoolEntry -> {
					if (!((Object) (templatePoolEntry.getValue()) instanceof StructureTemplatePoolInterface templatePoolInterface)) return;
					final List<Pair<StructurePoolElement, Integer>> additionalElements = context.getAdditionalElements(templatePoolEntry.getKey().identifier());
					if (!additionalElements.isEmpty()) templatePoolInterface.frozenlib$addTemplatePools(additionalElements);
				});
			});
		});
	}

	public static class TemplatePoolAdditionHolder {
		protected final Map<Identifier, List<Pair<StructurePoolElement, Integer>>> poolToElements = new Object2ObjectOpenHashMap<>();

		public void addElement(
			Identifier pool,
			Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> elementFunction,
			int weight,
			StructureTemplatePool.Projection projection
		) {
			final StructurePoolElement element = elementFunction.apply(projection);
			final List<Pair<StructurePoolElement, Integer>> list = this.poolToElements.getOrDefault(pool, new ArrayList<>());
			list.add(Pair.of(element, weight));
			this.poolToElements.put(pool, list);
		}

		public List<Pair<StructurePoolElement, Integer>> getAdditionalElements(Identifier pool) {
			return new ArrayList<>(this.poolToElements.getOrDefault(pool, ImmutableList.of()));
		}
	}

	@FunctionalInterface
	public interface AddAdditionalTemplatePools extends CommonEventEntrypoint {
		void addAdditionalTemplatePools(HolderLookup.RegistryLookup<StructureProcessorList> registry, TemplatePoolAdditionHolder context);
	}

}
