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

package net.frozenblock.lib.levelgen.structure.api.processor;

import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.levelgen.structure.impl.processor.StructureProcessorListAddition;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class StructureProcessorListAdditions {

	@ApiStatus.Internal
	public static Optional<StructureProcessorList> getAdditions(RegistryAccess registryAccess, Holder<Structure> structure) {
		return registryAccess.lookup(FrozenLibRegistries.STRUCTURE_PROCESSOR_LIST_ADDITION)
			.flatMap(registry -> registry.stream()
				.filter(addition -> addition.enabledAndMatches(structure))
				.findFirst()
				.map(addition -> addition.processors().value())
			);
	}

	@ApiStatus.Internal
	public static Optional<StructureProcessorList> getAdditions(RegistryAccess registryAccess, Structure structure) {
		return getAdditions(registryAccess, registryAccess.lookupOrThrow(Registries.STRUCTURE).wrapAsHolder(structure));
	}

	public static ResourceKey<StructureProcessorListAddition> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.STRUCTURE_PROCESSOR_LIST_ADDITION, id);
	}

	public static void register(
		BootstrapContext<StructureProcessorListAddition> context,
		ResourceKey<StructureProcessorListAddition> key,
		HolderSet<Structure> structures,
		List<StructureProcessor> processors
	) {
		context.register(key, new StructureProcessorListAddition(structures, processors, Optional.empty()));
	}

	public static void register(
		BootstrapContext<StructureProcessorListAddition> context,
		Identifier id,
		HolderSet<Structure> structures,
		List<StructureProcessor> processors
	) {
		register(context, createKey(id), structures, processors);
	}

	public static void register(
		BootstrapContext<StructureProcessorListAddition> context,
		ResourceKey<StructureProcessorListAddition> key,
		HolderSet<Structure> structures,
		List<StructureProcessor> processors,
		ConfigPredicate enabledWhen
	) {
		context.register(key, new StructureProcessorListAddition(structures, processors, Optional.of(enabledWhen)));
	}

	public static void register(
		BootstrapContext<StructureProcessorListAddition> context,
		Identifier id,
		HolderSet<Structure> structures,
		List<StructureProcessor> processors,
		ConfigPredicate enabledWhen
	) {
		register(context, createKey(id), structures, processors, enabledWhen);
	}
}
