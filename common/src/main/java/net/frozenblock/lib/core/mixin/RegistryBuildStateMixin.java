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

package net.frozenblock.lib.core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.frozenblock.lib.levelgen.biome.api.FrozenLibBiome;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistrySetBuilder.BootstrappedRegistryState.class)
public class RegistryBuildStateMixin {

	/**
	 * This is meant to fix {@code VanillaRegistries} lookup crashes we have.
	 */
	@WrapOperation(
		method = "lambda$errorOnMissingHolders$0",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
		)
	)
	private static boolean frozenLib$ignoreMissingBiomes(
		List<RuntimeException> instance, Object object, Operation<Boolean> original
	) {
		var element = (Holder.Reference<?>) object;
		var key = element.key();
		if (key.registryKey().equals(Registries.BIOME) && FrozenLibBiome.allFrozenLibBiomes().stream().anyMatch(biome -> biome.getKey().equals(key))) {
			return false;
		}
		return original.call(instance, object);
	}
}
