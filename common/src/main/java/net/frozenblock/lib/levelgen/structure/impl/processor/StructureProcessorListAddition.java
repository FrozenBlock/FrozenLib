/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.structure.impl.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public record StructureProcessorListAddition(
	HolderSet<Structure> structures,
	Holder<StructureProcessorList> processors,
	Optional<Holder<ConfigPredicate>> enabledWhen,
	Optional<Holder<ConfigPredicate>> serializationRequirement
) {
	private static final StructureProcessorListAddition EMPTY = new StructureProcessorListAddition(
		HolderSet.empty(),
		Holder.direct(new StructureProcessorList(List.of())),
		Optional.empty(),
		Optional.empty()
	);
	public static final MapCodec<StructureProcessorListAddition> DIRECT_CODEC = ConfigPredicate.HOLDER_CODEC.optionalFieldOf("serialization_requirement").dispatchMap(
		materialRuleAddition -> materialRuleAddition.serializationRequirement,
		requirement -> RecordCodecBuilder.mapCodec(instance -> {

			if (requirement.isPresent() && !requirement.get().value().test()) {
				return instance.point(EMPTY);
			}

			return instance.group(
				RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureProcessorListAddition::structures),
				StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(StructureProcessorListAddition::processors),
				ConfigPredicate.HOLDER_CODEC.optionalFieldOf("config_predicate").forGetter(StructureProcessorListAddition::enabledWhen),
				instance.point(requirement)
			).apply(instance, StructureProcessorListAddition::new);
		})
	);

	public StructureProcessorListAddition(HolderSet<Structure> structures, List<StructureProcessor> processors) {
		this(structures, Holder.direct(new StructureProcessorList(processors)), Optional.empty(), Optional.empty());
	}

	public StructureProcessorListAddition(HolderSet<Structure> structures, List<StructureProcessor> processors, Holder<ConfigPredicate> enabledWhen) {
		this(structures, Holder.direct(new StructureProcessorList(processors)), Optional.of(enabledWhen), Optional.empty());
	}

	public StructureProcessorListAddition(
		HolderSet<Structure> structures,
		List<StructureProcessor> processors,
		Holder<ConfigPredicate> enabledWhen,
		Holder<ConfigPredicate> serializationRequirement
	) {
		this(structures, processors, Optional.of(enabledWhen), Optional.of(serializationRequirement));
	}

	public StructureProcessorListAddition(
		HolderSet<Structure> structures,
		List<StructureProcessor> processors,
		Optional<Holder<ConfigPredicate>> enabledWhen,
		Optional<Holder<ConfigPredicate>> serializationRequirement
	) {
		this(structures, Holder.direct(new StructureProcessorList(processors)), enabledWhen, serializationRequirement);
	}

	public StructureProcessorListAddition(HolderSet<Structure> structures, Holder<StructureProcessorList> processors) {
		this(structures, processors, Optional.empty(), Optional.empty());
	}

	public StructureProcessorListAddition(HolderSet<Structure> structures, Holder<StructureProcessorList> processors, Holder<ConfigPredicate> enabledWhen) {
		this(structures, processors, Optional.of(enabledWhen), Optional.empty());
	}

	public boolean enabledAndMatches(Holder<Structure> structure) {
		return this.isEnabled() && this.matches(structure);
	}

	public boolean matches(Holder<Structure> structureHolder) {
		return !this.isEmpty() && this.structures.contains(structureHolder);
	}

	public boolean isEnabled() {
		return !this.isEmpty() && this.enabledWhen.map(Holder::value).map(ConfigPredicate::test).orElse(true);
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}
}
