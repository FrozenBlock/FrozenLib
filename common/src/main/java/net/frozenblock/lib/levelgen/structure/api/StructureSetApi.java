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

package net.frozenblock.lib.levelgen.structure.api;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.entrypoint.api.CommonEventEntrypoint;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;
import net.frozenblock.lib.levelgen.structure.impl.StructureSetAdditionInterface;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;

@UtilityClass
public class StructureSetApi {
	/**
	 * An event used to add new {@link Structure}s to {@link StructureSet}s.
	 */
	public static final Event<AddAdditionalStructures> ADD_ADDITIONAL_STRUCTURES = EventRegistry.createEnvironmentEvent(AddAdditionalStructures.class,
		callbacks -> (registry, structureSet, context) -> {
			for (var callback : callbacks) callback.addAdditionalStructures(registry, structureSet, context);
	});

	public static void init() {
		RegistryEvents.DYNAMIC_REGISTRY_LOADED.register(registryAccess -> {
			registryAccess.lookup(Registries.STRUCTURE_SET).ifPresent(structureSets -> {
				registryAccess.lookup(Registries.STRUCTURE).ifPresent(structures -> {
					structureSets.forEach(structureSet -> {
						if (!((Object)structureSet instanceof StructureSetAdditionInterface additionInterface)) return;
						ADD_ADDITIONAL_STRUCTURES.invoker().addAdditionalStructures(structures, structureSets.wrapAsHolder(structureSet), additionInterface);
					});
				});
			});
		});
	}

	@FunctionalInterface
	public interface AddAdditionalStructures extends CommonEventEntrypoint {
		void addAdditionalStructures(HolderLookup.RegistryLookup<Structure> structures, Holder<StructureSet> structureSet, StructureSetAdditionInterface context);
	}
}
