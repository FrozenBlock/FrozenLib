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

package net.frozenblock.lib.levelgen.material.api;

import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.levelgen.material.impl.MaterialRuleAddition;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class MaterialRuleAdditions {

	public static List<MaterialRuleAddition> getAll(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.MATERIAL_RULE_ADDITION).stream().toList();
	}

	public static List<MaterialRuleAddition> getAllFor(RegistryAccess registryAccess, Holder<DimensionType> dimension) {
		return getAll(registryAccess).stream().filter(materialRuleAddition -> materialRuleAddition.matches(dimension)).toList();
	}

	public static ResourceKey<MaterialRuleAddition> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.MATERIAL_RULE_ADDITION, id);
	}

	public static void register(
		BootstrapContext<MaterialRuleAddition> context,
		ResourceKey<MaterialRuleAddition> key,
		HolderSet<DimensionType> dimensions,
		MaterialRule materialRule
	) {
		register(context, key, dimensions, false, materialRule);
	}

	public static void register(
		BootstrapContext<MaterialRuleAddition> context,
		Identifier id,
		HolderSet<DimensionType> dimensions,
		MaterialRule materialRule
	) {
		register(context, createKey(id), dimensions, materialRule);
	}

	public static void register(
		BootstrapContext<MaterialRuleAddition> context,
		ResourceKey<MaterialRuleAddition> key,
		HolderSet<DimensionType> dimensions,
		boolean hasPreliminarySurface,
		MaterialRule materialRule
	) {
		context.register(key, new MaterialRuleAddition(dimensions, hasPreliminarySurface, materialRule));
	}

	public static void register(
		BootstrapContext<MaterialRuleAddition> context,
		Identifier id,
		HolderSet<DimensionType> dimensions,
		boolean hasPreliminarySurface,
		MaterialRule materialRule
	) {
		register(context, createKey(id), dimensions, hasPreliminarySurface, materialRule);
	}

	@ApiStatus.Internal
	public static Optional<MaterialRule> compileAndGet(RegistryAccess registryAccess, Holder<DimensionType> dimension) {
		final List<MaterialRuleAddition> materialRuleAdditions = getAllFor(registryAccess, dimension);
		if (materialRuleAdditions.isEmpty()) return Optional.empty();

		MaterialRule compiled = null;

		final List<MaterialRule> preliminarySurfaceMaterialRules = materialRuleAdditions.stream()
			.filter(MaterialRuleAddition::hasPreliminarySurface)
			.map(MaterialRuleAddition::materialRule)
			.toList();

		if (!preliminarySurfaceMaterialRules.isEmpty()) {
			MaterialRule sequenced = MaterialRules.sequence(preliminarySurfaceMaterialRules);
			sequenced = MaterialRules.ifTrue(MaterialRules.abovePreliminarySurface(), sequenced);
			compiled = sequenced;
		}

		// NO PRELIMINARY SURFACE
		final List<MaterialRule> noPreliminarySurfaceMaterialRules = materialRuleAdditions.stream()
			.filter(MaterialRuleAddition::noPreliminarySurface)
			.map(MaterialRuleAddition::materialRule)
			.toList();

		if (!noPreliminarySurfaceMaterialRules.isEmpty()) {
			final MaterialRule sequenced = MaterialRules.sequence(noPreliminarySurfaceMaterialRules);
			compiled = compiled == null ? sequenced : MaterialRules.sequence(sequenced, compiled);
		}

		return Optional.ofNullable(compiled);
	}
}
