/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.biome.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.lib.worldgen.biome.impl.BiomeInterface;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

	@ModifyReturnValue(
		method = "loadContentsFromNetwork",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/DataResult;getOrThrow()Ljava/lang/Object;"
		)
	)
	private static Object frozenLib$appendBiomeIDFromNetwork(
		Object original,
		@Local ResourceKey resourceKey
	) {
		if (original instanceof BiomeInterface biomeInterface) {
			biomeInterface.frozenLib$setBiomeID(resourceKey.location());
		}
		return original;
	}

	@ModifyReturnValue(
		method = "loadElementFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/DataResult;getOrThrow()Ljava/lang/Object;"
		)
	)
	private static Object frozenLib$appendBiomeIDFromResource(
		Object original,
		ResourceKey resourceKey
	) {
		if (original instanceof BiomeInterface biomeInterface) {
			biomeInterface.frozenLib$setBiomeID(resourceKey.location());
		}
		return original;
	}
}
