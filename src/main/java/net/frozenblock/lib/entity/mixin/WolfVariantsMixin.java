/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.entity.mixin;

import net.frozenblock.lib.entity.api.WolfVariantBiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.Optional;

@Mixin(WolfVariants.class)
public class WolfVariantsMixin {

	@Inject(
		method = "getSpawnVariant",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Registry;holders()Ljava/util/stream/Stream;",
			shift = At.Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILEXCEPTION,
		cancellable = true
	)
	private static void frozenLib$checkForNewBiomes(RegistryAccess registryManager, Holder<Biome> holder, CallbackInfoReturnable<Holder<WolfVariant>> info, Registry<WolfVariant> registry) {
		Optional<ResourceKey<Biome>> optionalBiome = holder.unwrapKey();
		if (optionalBiome.isPresent()) {
			ResourceKey<Biome> biomeKey = optionalBiome.get();
			Optional<ResourceKey<WolfVariant>> optionalVariant = WolfVariantBiomeRegistry.get(biomeKey);
            optionalVariant.ifPresent(wolfVariantResourceKey -> info.setReturnValue(registry.getHolderOrThrow(wolfVariantResourceKey)));
		}
	}

}
