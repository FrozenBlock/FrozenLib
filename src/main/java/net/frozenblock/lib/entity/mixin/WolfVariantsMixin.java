/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import java.util.Optional;
import net.frozenblock.lib.entity.api.WolfVariantBiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfVariants.class)
public class WolfVariantsMixin {

	@Inject(
		method = "selectVariantToSpawn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/Registry;listElements()Ljava/util/stream/Stream;"
		),
		cancellable = true
	)
	private static void frozenLib$checkForNewBiomes(
		RandomSource random, RegistryAccess registryAccess, SpawnContext context, CallbackInfoReturnable<Optional<? extends Holder<WolfVariant>>> info
	) {
		Registry<WolfVariant> registry = registryAccess.lookupOrThrow(Registries.WOLF_VARIANT);
		Optional<ResourceKey<Biome>> optionalBiome = context.biome().unwrapKey();
		if (optionalBiome.isPresent()) {
			ResourceKey<Biome> biomeKey = optionalBiome.get();
			Optional<ResourceKey<WolfVariant>> optionalVariant = WolfVariantBiomeRegistry.get(biomeKey);
            optionalVariant.ifPresent(wolfVariantResourceKey -> info.setReturnValue(registry.get(wolfVariantResourceKey)));
		}
	}

}
