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

package net.frozenblock.lib.worldgen.biome.impl;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.frozenblock.lib.FrozenSharedConstants;
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

	private static final Logger LOGGER = FrozenSharedConstants.LOGGER;

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
