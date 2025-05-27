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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VariantUtils.class)
public class VariantUtilsMixin {

	@Inject(
		method = "selectVariantToSpawn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/variant/PriorityProvider;pick(Ljava/util/stream/Stream;Ljava/util/function/Function;Lnet/minecraft/util/RandomSource;Ljava/lang/Object;)Ljava/util/Optional;"
		),
		cancellable = true
	)
	private static <T extends PriorityProvider<SpawnContext, ?>> void frozenLib$checkForNewBiomes(
		SpawnContext spawnContext, ResourceKey<Registry<T>> resourceKey, CallbackInfoReturnable<Optional<Holder.Reference<T>>> info
	) {
		if (!resourceKey.equals(Registries.WOLF_VARIANT)) return;
		Registry<WolfVariant> registry = spawnContext.level().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);
		Optional<ResourceKey<Biome>> optionalBiome = spawnContext.biome().unwrapKey();
		if (optionalBiome.isPresent()) {
			ResourceKey<Biome> biomeKey = optionalBiome.get();
			Optional<ResourceKey<WolfVariant>> optionalVariant = WolfVariantBiomeRegistry.get(biomeKey);
            optionalVariant.ifPresent(wolfVariantResourceKey -> info.setReturnValue(
				(Optional<Holder.Reference<T>>) (Object) registry.get(wolfVariantResourceKey)
			));
		}
	}

}
