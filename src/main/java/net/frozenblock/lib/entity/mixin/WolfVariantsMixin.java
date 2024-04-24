/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
