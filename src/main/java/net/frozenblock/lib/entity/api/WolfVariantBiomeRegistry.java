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

package net.frozenblock.lib.entity.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WolfVariantBiomeRegistry {
	private static final Map<ResourceKey<Biome>, ResourceKey<WolfVariant>> WOLF_VARIANT_FROM_BIOME = new Object2ObjectOpenHashMap<>();

	public static void register(@NotNull ResourceKey<Biome> biome, @NotNull ResourceKey<WolfVariant> wolfVariant) {
		WOLF_VARIANT_FROM_BIOME.put(biome, wolfVariant);
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
		return WOLF_VARIANT_FROM_BIOME.getOrDefault(biome, null);
	}

}
