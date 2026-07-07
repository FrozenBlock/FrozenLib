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
import net.frozenblock.lib.levelgen.material.impl.RuleSourceAddition;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public final class RuleSourceAdditions {

	public static List<RuleSourceAddition> getAll(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.RULE_SOURCE_ADDITION).stream().toList();
	}

	public static List<RuleSourceAddition> getAllFor(RegistryAccess registryAccess, Holder<DimensionType> dimension) {
		return getAll(registryAccess).stream().filter(ruleSourceAddition -> ruleSourceAddition.matches(dimension)).toList();
	}

	public static ResourceKey<RuleSourceAddition> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.RULE_SOURCE_ADDITION, id);
	}

	public static void register(
		BootstrapContext<RuleSourceAddition> context,
		ResourceKey<RuleSourceAddition> key,
		HolderSet<DimensionType> dimensions,
		SurfaceRules.RuleSource ruleSource
	) {
		register(context, key, dimensions, false, ruleSource);
	}

	public static void register(
		BootstrapContext<RuleSourceAddition> context,
		Identifier id,
		HolderSet<DimensionType> dimensions,
		SurfaceRules.RuleSource ruleSource
	) {
		register(context, createKey(id), dimensions, ruleSource);
	}

	public static void register(
		BootstrapContext<RuleSourceAddition> context,
		ResourceKey<RuleSourceAddition> key,
		HolderSet<DimensionType> dimensions,
		boolean hasPreliminarySurface,
		SurfaceRules.RuleSource ruleSource
	) {
		context.register(key, new RuleSourceAddition(dimensions, hasPreliminarySurface, ruleSource));
	}

	public static void register(
		BootstrapContext<RuleSourceAddition> context,
		Identifier id,
		HolderSet<DimensionType> dimensions,
		boolean hasPreliminarySurface,
		SurfaceRules.RuleSource ruleSource
	) {
		register(context, createKey(id), dimensions, hasPreliminarySurface, ruleSource);
	}

	@ApiStatus.Internal
	public static Optional<SurfaceRules.RuleSource> compileAndGet(RegistryAccess registryAccess, Holder<DimensionType> dimension) {
		final List<RuleSourceAddition> ruleSourceAdditions = getAllFor(registryAccess, dimension);
		if (ruleSourceAdditions.isEmpty()) return Optional.empty();

		SurfaceRules.RuleSource compiled = null;

		final List<SurfaceRules.RuleSource> preliminarySurfaceRuleSources = ruleSourceAdditions.stream()
			.filter(RuleSourceAddition::hasPreliminarySurface)
			.map(RuleSourceAddition::ruleSource)
			.toList();

		if (!preliminarySurfaceRuleSources.isEmpty()) {
			SurfaceRules.RuleSource sequenced = FrozenLibMaterialRules.sequence(preliminarySurfaceRuleSources);
			sequenced = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), sequenced);
			compiled = sequenced;
		}

		// NO PRELIMINARY SURFACE
		final List<SurfaceRules.RuleSource> noPreliminarySurfaceRuleSources = ruleSourceAdditions.stream()
			.filter(RuleSourceAddition::noPreliminarySurface)
			.map(RuleSourceAddition::ruleSource)
			.toList();

		if (!noPreliminarySurfaceRuleSources.isEmpty()) {
			final SurfaceRules.RuleSource sequenced = FrozenLibMaterialRules.sequence(noPreliminarySurfaceRuleSources);
			compiled = compiled == null ? sequenced : SurfaceRules.sequence(sequenced, compiled);
		}

		return Optional.ofNullable(compiled);
	}
}
