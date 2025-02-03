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

package net.frozenblock.lib.worldgen.biome.impl;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public final class OverworldBiomeData {

	private static final Set<ResourceKey<Biome>> OVERWORLD_BIOMES = new ObjectOpenHashSet<>();

	private static final Map<ResourceKey<Biome>, Climate.ParameterPoint> OVERWORLD_BIOME_NOISE_POINTS = new Object2ObjectOpenHashMap<>();

	private static final Logger LOGGER = FrozenLibConstants.LOGGER;

	private OverworldBiomeData() {
	}

	public static void addOverworldBiome(ResourceKey<Biome> biome, Climate.ParameterPoint spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "Climate.ParameterPoint is null");
		OVERWORLD_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
		clearBiomeSourceCache();
	}

	public static Map<ResourceKey<Biome>, Climate.ParameterPoint> getOverworldBiomeNoisePoints() {
		return OVERWORLD_BIOME_NOISE_POINTS;
	}

    public static boolean canGenerateInOverworld(ResourceKey<Biome> biome) {
            return MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD.usedBiomes().anyMatch(input -> input.equals(biome));
	}

	private static void clearBiomeSourceCache() {
		OVERWORLD_BIOMES.clear(); // Clear cached biome source data
	}

	public static <T> Climate.ParameterList<T> withModdedBiomeEntries(Climate.ParameterList<T> entries, Function<ResourceKey<Biome>, T> biomes) {
		if (OVERWORLD_BIOME_NOISE_POINTS.isEmpty()) {
			return entries;
		}

		ArrayList<Pair<Climate.ParameterPoint, T>> entryList = new ArrayList<>(entries.values());

        for (Map.Entry<ResourceKey<Biome>, Climate.ParameterPoint> entry : OVERWORLD_BIOME_NOISE_POINTS.entrySet()) {
			entryList.add(Pair.of(entry.getValue(), biomes.apply(entry.getKey())));
        }

		return new Climate.ParameterList<>(entryList);
	}
}
