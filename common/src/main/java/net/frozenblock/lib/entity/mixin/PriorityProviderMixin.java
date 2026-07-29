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

package net.frozenblock.lib.entity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.frozenblock.lib.entity.api.variant.VariantSpawnInjection;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PriorityProvider.class)
public interface PriorityProviderMixin {

	@ModifyExpressionValue(
		method = "select",
		at = @At(
			value = "NEW",
			target = "()Ljava/util/ArrayList;"
		)
	)
	private static ArrayList frozenLib$appendInjectedSpawnConditions(
		ArrayList original,
		@Local(argsOnly = true) LocalRef<Stream<Object>> mutableEntries,
		@Local(argsOnly = true) Object context
	) {
		if (!(context instanceof SpawnContext spawnContext)) return original;

		final Optional<Registry<VariantSpawnInjection>> spawnInjections = spawnContext.level().registryAccess().lookup(FrozenLibRegistries.VARIANT_SPAWN_INJECTION);
		if (spawnInjections.isEmpty()) return original;

		final List<Object> cachedEntries = mutableEntries.get().toList();
		cachedEntries.forEach(entry -> {
			spawnInjections.get().stream()
				.filter(injection -> injection.matchesVariant(entry))
				.toList()
				.stream().map(VariantSpawnInjection::spawnConditions)
				.forEach(selectors -> {
					selectors.selectors().forEach(selector -> {
						original.add(
							new PriorityProvider.UnpackedEntry(
								entry,
								selector.priority(),
								DataFixUtils.orElseGet(selector.condition(), PriorityProvider.SelectorCondition::alwaysTrue)
							)
						);
					});
				});
		});
		mutableEntries.set(cachedEntries.stream());

		return original;
	}
}
