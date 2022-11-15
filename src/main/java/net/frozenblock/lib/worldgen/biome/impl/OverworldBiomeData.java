/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.worldgen.biome.impl;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.frozenblock.lib.worldgen.biome.api.FrozenBiomeSourceAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
public final class OverworldBiomeData {

    private static final Set<ResourceKey<Biome>> OVERWORLD_BIOMES = new HashSet<>();

    private static final Map<ResourceKey<Biome>, Climate.ParameterPoint> OVERWORLD_BIOME_NOISE_POINTS = new HashMap<>();

    private static final Logger LOGGER = FrozenMain.LOGGER;

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
        if (OVERWORLD_BIOMES.isEmpty()) {
			var biomeRegistry = FrozenRegistry.vanillaRegistries().lookupOrThrow(Registries.BIOME);

            MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(FrozenRegistry.vanillaRegistries().lookupOrThrow(Registries.BIOME));

            for (Holder<Biome> entry : source.possibleBiomes()) {
                entry.unwrapKey().ifPresent(OVERWORLD_BIOMES::add);
            }

            biomeRegistry.getOrThrow(BiomeTags.IS_OVERWORLD).forEach(entry -> {
                if (!OVERWORLD_BIOMES.contains(entry.unwrapKey().orElseThrow())) {
                    entry.unwrapKey().ifPresent(OVERWORLD_BIOMES::add);
                }
            });
        }

        return OVERWORLD_BIOMES.contains(biome) || OVERWORLD_BIOME_NOISE_POINTS.containsKey(biome);
    }

    private static void clearBiomeSourceCache() {
        OVERWORLD_BIOMES.clear(); // Clear cached biome source data
    }

    private static Climate.ParameterList<Holder<Biome>> withModdedBiomeEntries(Climate.ParameterList<Holder<Biome>> entries, HolderGetter<Biome> biomes) {
        if (OVERWORLD_BIOME_NOISE_POINTS.isEmpty()) {
            return entries;
        }

        ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>> entryList = new ArrayList<>(entries.values());

        for (Map.Entry<ResourceKey<Biome>, Climate.ParameterPoint> entry : OVERWORLD_BIOME_NOISE_POINTS.entrySet()) {
			Holder.Reference<Biome> biomeEntry = biomes.get(entry.getKey()).orElse(null);

            if (biomeEntry != null) {
                entryList.add(Pair.of(entry.getValue(), biomeEntry));
            } else {
                LOGGER.warn("Overworld biome {} not loaded", entry.getKey().location());
            }
        }

        return new Climate.ParameterList<>(entryList);
    }

    public static void modifyBiomeSource(HolderGetter<Biome> biomeRegistry, BiomeSource biomeSource) {
        if (biomeSource instanceof MultiNoiseBiomeSource multiNoiseBiomeSource) {
            if (((FrozenBiomeSourceAccess) multiNoiseBiomeSource).frozenLib_shouldModifyBiomeEntries() && multiNoiseBiomeSource.stable(MultiNoiseBiomeSource.Preset.OVERWORLD)) {
                multiNoiseBiomeSource.parameters = OverworldBiomeData.withModdedBiomeEntries(
                        MultiNoiseBiomeSource.Preset.OVERWORLD.parameterSource.apply(biomeRegistry),
                        biomeRegistry);
                multiNoiseBiomeSource.possibleBiomes = multiNoiseBiomeSource.parameters.values().stream().map(Pair::getSecond).collect(Collectors.toSet());
                ((FrozenBiomeSourceAccess) multiNoiseBiomeSource).frozenLib_setModifyBiomeEntries(false);
            }
        }
    }
}
