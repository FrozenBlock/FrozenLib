/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.surface.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ServerLevelEvents;
import net.frozenblock.lib.levelgen.surface.api.FrozenLibSurfaceRules;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

@UtilityClass
public class SurfaceRuleUtil {

	public static void injectSurfaceRules(NoiseGeneratorSettings settings, HolderLookup<Biome> biomes, ResourceKey<DimensionType> dimension) {
		final NoiseGeneratorInterface noiseGenerator = NoiseGeneratorInterface.class.cast(settings);
		final SurfaceRules.RuleSource newRules = FrozenLibSurfaceRules.getSurfaceRules(biomes, dimension);
		if (newRules != null) noiseGenerator.frozenLib$writeSurfaceRules(newRules);
	}

	public static void init() {
		ServerLevelEvents.LOAD.register((server, level) -> {
			final RegistryAccess registries = server.registryAccess();
			final Registry<LevelStem> levelStems = registries.lookupOrThrow(Registries.LEVEL_STEM);

			final Registry<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);

			final ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();
			if (!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator)) return;

			final var noiseSettings = noiseGenerator.generatorSettings().value();
			injectSurfaceRules(noiseSettings, biomes, level.dimensionTypeRegistration().unwrapKey().orElseThrow());
		});
	}
}
