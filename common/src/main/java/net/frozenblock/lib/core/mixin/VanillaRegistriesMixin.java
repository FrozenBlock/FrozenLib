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

import net.frozenblock.lib.levelgen.biome.api.FrozenLibBiome;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {

	/**
	 * This is meant to fix {@code VanillaRegistries} lookup crashes we have.
	 */
	@Inject(
		method = "lambda$validateThatAllBiomeFeaturesHaveBiomeFilter$0",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void frozenLib$ignoreMissingBiomes(HolderLookup.RegistryLookup placedFeatures, Holder.Reference<Biome> biome, CallbackInfo info) {
		if (!biome.isBound()
			&& biome.key().registryKey().equals(Registries.BIOME)
			&& FrozenLibBiome.allFrozenLibBiomes().stream().anyMatch(frozenLibBiome -> frozenLibBiome.getKey().equals(biome.key()))
		) {
			info.cancel();
		}
	}
}
