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

package net.frozenblock.lib.levelgen.structure.api.placement;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

@UtilityClass
public class StructurePlacementExclusionApi {
	/**
	 * An event used to add new placement exclusions for a {@link StructureSet}.
	 * <p>
	 * This is used to prevent certain {@link StructureSet}s from generating too close to other {@link StructureSet}s.
	 */
	public static final Event<AddExclusion> ADD_PLACEMENT_EXCLUSIONS = EventRegistry.createEnvironmentEvent(AddExclusion.class,
		callbacks -> (structureSet, context) -> {
			for (var callback : callbacks) callback.addExclusions(structureSet, context);
		});

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			registryAccess.lookup(Registries.STRUCTURE_SET).ifPresent(structureSetRegistry -> {
				structureSetRegistry.listElements().forEach(structureSet -> {
					final Context context = new Context(structureSetRegistry);
					ADD_PLACEMENT_EXCLUSIONS.invoker().addExclusions(structureSet, context);
					structureSet.value().frozenLib$addExclusions(context.exclusions);
				});
			});
		});
	}

	@FunctionalInterface
	public interface AddExclusion extends CommonEventEntrypoint {
		void addExclusions(Holder<StructureSet> structureSet, Context context);
	}

	public final class Context {
		private final HolderLookup.RegistryLookup<StructureSet> structureSets;
		private final List<Pair<Holder<StructureSet>, Integer>> exclusions;

		private Context(HolderLookup.RegistryLookup<StructureSet> structureSets) {
			this.structureSets = structureSets;
			this.exclusions = new ArrayList<>();
		}

		public void add(Holder<StructureSet> otherSet, int chunkCount) {
			this.exclusions.add(Pair.of(otherSet, chunkCount));
		}

		public void add(ResourceKey<StructureSet> otherSet, int chunkCount) {
			this.add(this.structureSets.getOrThrow(otherSet), chunkCount);
		}

		public void add(Identifier otherSet, int chunkCount) {
			this.add(ResourceKey.create(Registries.STRUCTURE_SET, otherSet), chunkCount);
		}

		public void add(String otherSet, int chunkCount) {
			this.add(Identifier.parse(otherSet), chunkCount);
		}

		public HolderLookup.RegistryLookup<StructureSet> structureSets() {
			return this.structureSets;
		}
	}
}
