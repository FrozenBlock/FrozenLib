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

package net.frozenblock.lib.worldgen.biome.api;

import java.util.List;
import net.frozenblock.lib.worldgen.biome.api.parameters.FrozenBiomeParameters;
import net.frozenblock.lib.worldgen.biome.impl.OverworldBiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;

/**
 * API that exposes the internals of Minecraft's overworld biome code.
 */
public final class FrozenOverworldBiomes {
    private FrozenOverworldBiomes() {
		throw new UnsupportedOperationException("FrozenOverworldBiomes contains only static declarations.");
    }

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The biome to add. Must not be null.
	 * @param targetPoint	data about the given {@link Biome}'s spawning information in the Overworld.
	 * @see Climate.TargetPoint
	 */
    public static void addOverworldBiome(ResourceKey<Biome> biome, Climate.@NotNull TargetPoint targetPoint) {
        OverworldBiomeData.addOverworldBiome(biome, Climate.parameters(
                targetPoint.temperature(),
                targetPoint.humidity(),
                targetPoint.continentalness(),
                targetPoint.erosion(),
                targetPoint.depth(),
                targetPoint.weirdness(),
                0
        ));
    }

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome				The {@link Biome} to add. Must not be null.
	 * @param parameterPoint	data about the given {@link Biome}'s spawning information in the Overworld.
	 * @see Climate.ParameterPoint
	 */
    public static void addOverworldBiome(ResourceKey<Biome> biome, Climate.ParameterPoint parameterPoint) {
        OverworldBiomeData.addOverworldBiome(biome, parameterPoint);
    }

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The {@link Biome} to add. Must not be null.
	 * @param weirdnesses	The specific weirdnesses the biome should be added to.
	 */
	public static void addOverworldBiome(
			ResourceKey<Biome> biome,
			Climate.Parameter temperature,
			Climate.Parameter humidity,
			Climate.Parameter continentalness,
			Climate.Parameter erosion,
			float offset,
			Climate.Parameter... weirdnesses
	) {
		addOverworldBiome(biome, temperature, humidity, continentalness, erosion, offset, List.of(weirdnesses));
	}

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The {@link Biome} to add. Must not be null.
	 * @param weirdnesses	The specific weirdnesses the biome should be added to.
	 */
	public static void addOverworldBiome(
			ResourceKey<Biome> biome,
			Climate.Parameter temperature,
			Climate.Parameter humidity,
			Climate.Parameter continentalness,
			Climate.Parameter erosion,
			float offset,
			List<Climate.Parameter> weirdnesses
	) {
		addOverworldBiome(biome, temperature, humidity, continentalness, Climate.Parameter.point(0.0F), erosion, offset, weirdnesses);
		addOverworldBiome(biome, temperature, humidity, continentalness, Climate.Parameter.point(1.0F), erosion, offset, weirdnesses);
	}

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The {@link Biome} to add. Must not be null.
	 * @param weirdnesses	The specific weirdnesses the biome should be added to.
	 */
	public static void addOverworldBiome(
			ResourceKey<Biome> biome,
			Climate.Parameter temperature,
			Climate.Parameter humidity,
			Climate.Parameter continentalness,
			Climate.Parameter erosion,
			Climate.Parameter depth,
			float offset,
			Climate.Parameter... weirdnesses
	) {
		addOverworldBiome(biome, temperature, humidity, continentalness, erosion, depth, offset, List.of(weirdnesses));
	}

	/**
	 * Adds a biome to the Overworld generator.
	 *
	 * @param biome			The {@link Biome} to add. Must not be null.
	 * @param weirdnesses	The specific weirdnesses the biome should be added to.
	 */
	public static void addOverworldBiome(
			ResourceKey<Biome> biome,
			Climate.Parameter temperature,
			Climate.Parameter humidity,
			Climate.Parameter continentalness,
			Climate.Parameter erosion,
			Climate.Parameter depth,
			float offset,
			List<Climate.Parameter> weirdnesses
	) {
		FrozenBiomeParameters.addWeirdness(weirdness -> OverworldBiomeData.addOverworldBiome(
				biome, Climate.parameters(
						temperature,
						humidity,
						continentalness,
						erosion,
						depth,
						weirdness,
						offset
				)
		), weirdnesses);
	}

	/**
	 * Returns true if the given biome can generate in the Overworld, considering the Vanilla Overworld biomes,
	 * and any biomes added to the Overworld by mods.
	 */
    public static boolean canGenerateInOverworld(ResourceKey<Biome> biome) {
        return OverworldBiomeData.canGenerateInOverworld(biome);
    }
}
