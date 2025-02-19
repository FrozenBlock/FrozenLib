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
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tool to easily add a {@link WolfVariant} to a biome without impacting data-driven content.
 *
 * <p> Multiple {@link WolfVariant}s can be added to a single biome, which will result in a random {@link WolfVariant} being picked each spawn.
 */
public class WolfVariantBiomeRegistry {
	private static final Map<ResourceKey<Biome>, List<ResourceKey<WolfVariant>>> WOLF_VARIANT_FROM_BIOME = new Object2ObjectOpenHashMap<>();

	/**
	 * Registers a {@link WolfVariant} to a biome.
	 * @param biome The biome to register the {@link WolfVariant} to.
	 * @param wolfVariant the {@link WolfVariant} to be added.
	 */
	public static void register(@NotNull ResourceKey<Biome> biome, @NotNull ResourceKey<WolfVariant> wolfVariant) {
		List<ResourceKey<WolfVariant>> variantList = WOLF_VARIANT_FROM_BIOME.getOrDefault(biome, null);
		if (variantList == null) {
			variantList = new ArrayList<>();
			WOLF_VARIANT_FROM_BIOME.put(biome, variantList);
		}

		variantList.add(wolfVariant);
	}

	/**
	 * Returns a registered {@link WolfVariant} in a biome, if possible, in {@link ResourceKey} form.
	 *
	 * @param biome The biome to check for a registered {@link WolfVariant} in.
	 * @return the found {@link WolfVariant}, if possible, in {@link ResourceKey} form.
	 */
	@NotNull
	public static Optional<ResourceKey<WolfVariant>> get(ResourceKey<Biome> biome) {
		return Optional.ofNullable(getVariantOrNull(biome));
	}

	/**
	 * Returns a registered {@link WolfVariant} in a biome, if possible.
	 *
	 * @param biome The biome to check for a registered {@link WolfVariant} in.
	 * @return the found {@link WolfVariant}, if possible.
	 */
	@NotNull
	public static Optional<WolfVariant> get(@NotNull RegistryAccess registryManager, ResourceKey<Biome> biome) {
		Registry<WolfVariant> registry = registryManager.lookupOrThrow(Registries.WOLF_VARIANT);
		return registry.getOptional(getVariantOrNull(biome));
	}

	/**
	 * Returns a registered {@link WolfVariant} in a biome in {@link ResourceKey} form, or null if none is registered.
	 *
	 * @param biome The biome to check for a registered {@link WolfVariant} in.
	 * @return the found {@link WolfVariant} in {@link ResourceKey} form, or null if not found.
	 */
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
