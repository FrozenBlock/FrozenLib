/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.entity.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.math.api.AdvancedMath;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WolfVariantBiomeRegistry {
	private static final Map<ResourceKey<Biome>, List<ResourceKey<WolfVariant>>> WOLF_VARIANT_FROM_BIOME = new Object2ObjectOpenHashMap<>();

	public static void register(@NotNull ResourceKey<Biome> biome, @NotNull ResourceKey<WolfVariant> wolfVariant) {
		List<ResourceKey<WolfVariant>> variantList = WOLF_VARIANT_FROM_BIOME.getOrDefault(biome, null);
		if (variantList == null) {
			variantList = new ArrayList<>();
			WOLF_VARIANT_FROM_BIOME.put(biome, variantList);
		}

		variantList.add(wolfVariant);
	}

	@NotNull
	public static Optional<ResourceKey<WolfVariant>> get(ResourceKey<Biome> biome) {
		return Optional.ofNullable(getVariantOrNull(biome));
	}

	@NotNull
	public static Optional<WolfVariant> get(@NotNull RegistryAccess registryManager, ResourceKey<Biome> biome) {
		Registry<WolfVariant> registry = registryManager.registryOrThrow(Registries.WOLF_VARIANT);
		return registry.getOptional(getVariantOrNull(biome));
	}

	@Nullable
	private static ResourceKey<WolfVariant> getVariantOrNull(ResourceKey<Biome> biome) {
		List<ResourceKey<WolfVariant>> variantList = WOLF_VARIANT_FROM_BIOME.getOrDefault(biome, null);
		if (variantList != null && !variantList.isEmpty()) {
			int size = variantList.size();
			return variantList.get(AdvancedMath.random().nextInt(size));
		}
		return null;
	}

}
