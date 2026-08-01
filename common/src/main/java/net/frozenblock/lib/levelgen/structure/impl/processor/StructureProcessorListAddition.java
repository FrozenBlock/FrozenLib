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

import com.mojang.serialization.Codec;
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

public record StructureProcessorListAddition(HolderSet<Structure> structures, Holder<StructureProcessorList> processors, Optional<ConfigPredicate> enabledWhen) {
	public static final Codec<StructureProcessorListAddition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureProcessorListAddition::structures),
		StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(StructureProcessorListAddition::processors),
		ConfigPredicate.CODEC.optionalFieldOf("config_predicate").forGetter(StructureProcessorListAddition::enabledWhen)
	).apply(instance, StructureProcessorListAddition::new));

	public StructureProcessorListAddition(HolderSet<Structure> structures, Holder<StructureProcessorList> processors) {
		this(structures, processors, Optional.empty());
	}

	public StructureProcessorListAddition(HolderSet<Structure> structures, List<StructureProcessor> processors, Optional<ConfigPredicate> enabledWhen) {
		this(structures, Holder.direct(new StructureProcessorList(processors)), enabledWhen);
	}

	public StructureProcessorListAddition(HolderSet<Structure> structures, List<StructureProcessor> processors) {
		this(structures, processors, Optional.empty());
	}

	public boolean enabledAndMatches(Holder<Structure> structure) {
		return this.isEnabled() && this.matches(structure);
	}

	public boolean matches(Holder<Structure> structureHolder) {
		return this.structures.contains(structureHolder);
	}

	public boolean isEnabled() {
		return this.enabledWhen.map(ConfigPredicate::test).orElse(true);
	}
}
