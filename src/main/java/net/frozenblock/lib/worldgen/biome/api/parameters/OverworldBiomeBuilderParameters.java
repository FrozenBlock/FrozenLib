/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import net.frozenblock.lib.worldgen.biome.api.BiomeParameters;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

public class OverworldBiomeBuilderParameters {
	public static final float VALLEY_SIZE = 0.05F;
	public static final float LOW_START = 0.26666668F;
	public static final float HIGH_START = 0.4F;
	public static final float HIGH_END = 0.93333334F;
	public static final float PEAK_SIZE = 0.1F;
	public static final float PEAK_START = 0.56666666F;
	public static final float PEAK_END = 0.7666667F;
	public static final float NEAR_INLAND_START = -0.11F;
	public static final float MID_INLAND_START = 0.03F;
	public static final float FAR_INLAND_START = 0.3F;
	public static final float EROSION_INDEX_1_START = -0.78F;
	public static final float EROSION_INDEX_2_START = -0.375F;
	private static final float EROSION_DEEP_DARK_DRYNESS_THRESHOLD = -0.225F;
	private static final float DEPTH_DEEP_DARK_DRYNESS_THRESHOLD = 0.9F;
	public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	public static final Climate.Parameter[] temperatures = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.45F), Climate.Parameter.span(-0.45F, -0.15F), Climate.Parameter.span(-0.15F, 0.2F), Climate.Parameter.span(0.2F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
	public static final Climate.Parameter[] humidities = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.35F), Climate.Parameter.span(-0.35F, -0.1F), Climate.Parameter.span(-0.1F, 0.1F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(0.3F, 1.0F)};
	public static final Climate.Parameter[] erosions = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.78F), Climate.Parameter.span(-0.78F, -0.375F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.span(-0.2225F, 0.05F), Climate.Parameter.span(0.05F, 0.45F), Climate.Parameter.span(0.45F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
	public static final Climate.Parameter FROZEN_RANGE = temperatures[0];
	public static final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(temperatures[1], temperatures[4]);
	public static final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2F, -1.05F);
	public static final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05F, -0.455F);
	public static final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455F, -0.19F);
	public static final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19F, -0.11F);
	public static final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11F, 0.55F);
	public static final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11F, 0.03F);
	public static final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03F, 0.3F);
	public static final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3F, 1.0F);
	public static final ResourceLocation[][] OCEANS = new ResourceLocation[][]{{Biomes.DEEP_FROZEN_OCEAN.location(), Biomes.DEEP_COLD_OCEAN.location(), Biomes.DEEP_OCEAN.location(), Biomes.DEEP_LUKEWARM_OCEAN.location(), Biomes.WARM_OCEAN.location()}, {Biomes.FROZEN_OCEAN.location(), Biomes.COLD_OCEAN.location(), Biomes.OCEAN.location(), Biomes.LUKEWARM_OCEAN.location(), Biomes.WARM_OCEAN.location()}};
	public static final ResourceLocation[][] MIDDLE_BIOMES = new ResourceLocation[][]{{Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_TAIGA.location(), Biomes.TAIGA.location()}, {Biomes.PLAINS.location(), Biomes.PLAINS.location(), Biomes.FOREST.location(), Biomes.TAIGA.location(), Biomes.OLD_GROWTH_SPRUCE_TAIGA.location()}, {Biomes.FLOWER_FOREST.location(), Biomes.PLAINS.location(), Biomes.FOREST.location(), Biomes.BIRCH_FOREST.location(), Biomes.DARK_FOREST.location()}, {Biomes.SAVANNA.location(), Biomes.SAVANNA.location(), Biomes.FOREST.location(), Biomes.JUNGLE.location(), Biomes.JUNGLE.location()}, {Biomes.DESERT.location(), Biomes.DESERT.location(), Biomes.DESERT.location(), Biomes.DESERT.location(), Biomes.DESERT.location()}};
	public static final ResourceLocation[][] MIDDLE_BIOMES_VARIANT = new ResourceLocation[][]{{Biomes.ICE_SPIKES.location(), null, Biomes.SNOWY_TAIGA.location(), null, null}, {null, null, null, null, Biomes.OLD_GROWTH_PINE_TAIGA.location()}, {Biomes.SUNFLOWER_PLAINS.location(), null, null, Biomes.OLD_GROWTH_BIRCH_FOREST.location(), null}, {null, null, Biomes.PLAINS.location(), Biomes.SPARSE_JUNGLE.location(), Biomes.BAMBOO_JUNGLE.location()}, {null, null, null, null, null}};
	public static final ResourceLocation[][] PLATEAU_BIOMES = new ResourceLocation[][]{{Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_PLAINS.location(), Biomes.SNOWY_TAIGA.location(), Biomes.SNOWY_TAIGA.location()}, {Biomes.MEADOW.location(), Biomes.MEADOW.location(), Biomes.FOREST.location(), Biomes.TAIGA.location(), Biomes.OLD_GROWTH_SPRUCE_TAIGA.location()}, {Biomes.MEADOW.location(), Biomes.MEADOW.location(), Biomes.MEADOW.location(), Biomes.MEADOW.location(), Biomes.DARK_FOREST.location()}, {Biomes.SAVANNA_PLATEAU.location(), Biomes.SAVANNA_PLATEAU.location(), Biomes.FOREST.location(), Biomes.FOREST.location(), Biomes.JUNGLE.location()}, {Biomes.BADLANDS.location(), Biomes.BADLANDS.location(), Biomes.BADLANDS.location(), Biomes.WOODED_BADLANDS.location(), Biomes.WOODED_BADLANDS.location()}};
	public static final ResourceLocation[][] PLATEAU_BIOMES_VARIANT = new ResourceLocation[][]{{Biomes.ICE_SPIKES.location(), null, null, null, null}, {null, null, Biomes.MEADOW.location(), Biomes.MEADOW.location(), Biomes.OLD_GROWTH_PINE_TAIGA.location()}, {null, null, Biomes.FOREST.location(), Biomes.BIRCH_FOREST.location(), null}, {null, null, null, null, null}, {Biomes.ERODED_BADLANDS.location(), Biomes.ERODED_BADLANDS.location(), null, null, null}};
	public static final ResourceLocation[][] SHATTERED_BIOMES = new ResourceLocation[][]{{Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), Biomes.WINDSWEPT_HILLS.location(), Biomes.WINDSWEPT_FOREST.location(), Biomes.WINDSWEPT_FOREST.location()}, {Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), Biomes.WINDSWEPT_GRAVELLY_HILLS.location(), Biomes.WINDSWEPT_HILLS.location(), Biomes.WINDSWEPT_FOREST.location(), Biomes.WINDSWEPT_FOREST.location()}, {Biomes.WINDSWEPT_HILLS.location(), Biomes.WINDSWEPT_HILLS.location(), Biomes.WINDSWEPT_HILLS.location(), Biomes.WINDSWEPT_FOREST.location(), Biomes.WINDSWEPT_FOREST.location()}, {null, null, null, null, null}, {null, null, null, null, null}};

	public static final HashMap<ResourceLocation, BiomeParameters> BIOMES = new LinkedHashMap<>();
	private static boolean hasRun = false;

	public static BiomeParameters getParameters(ResourceLocation location) {
		runBiomes();
		return getOrCreateParameters(location);
	}

	private static void runBiomes() {
		if (!hasRun) {
			hasRun = true;
			addBiomes(pair -> addParameters(pair.getFirst(), pair.getSecond()));
		}
	}

	public static BiomeParameters getParameters(ResourceKey<Biome> key) {
		return getParameters(key.location());
	}

	private static void addParameters(Climate.ParameterPoint parameters, ResourceLocation location) {
		BiomeParameters biomeParameters = getOrCreateParameters(location);
		biomeParameters.points.add(parameters);
	}

	private static BiomeParameters getOrCreateParameters(ResourceLocation location) {
		if (BIOMES.containsKey(location)) {
			return BIOMES.get(location);
		} else {
			BiomeParameters parameters = new BiomeParameters();
			BIOMES.put(location, parameters);
			return parameters;
		}
	}

	public static List<Climate.ParameterPoint> points(BiomeParameters parameters) {
		return parameters.points;
	}

	public static List<Climate.ParameterPoint> points(ResourceKey<Biome> key) {
		return points(key.location());
	}

	public static List<Climate.ParameterPoint> points(ResourceLocation location) {
		return points(OverworldBiomeBuilderParameters.getParameters(location));
	}

	private static void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> key) {
		if (!SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
			addOffCoastBiomes(key);
			addInlandBiomes(key);
			addUndergroundBiomes(key);
		}
	}

	private static List<Climate.ParameterPoint> OFF_COAST_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getOffCoastPoints() {
		runBiomes();
		return OFF_COAST_POINTS;
	}

	private static void addOffCoastBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer) {
		addSurfaceBiome(consumer, FULL_RANGE, FULL_RANGE, mushroomFieldsContinentalness, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.MUSHROOM_FIELDS.location());

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];
			addSurfaceBiome(consumer, parameter, FULL_RANGE, deepOceanContinentalness, FULL_RANGE, FULL_RANGE, 0.0F, OCEANS[0][i]);
			addSurfaceBiome(consumer, parameter, FULL_RANGE, oceanContinentalness, FULL_RANGE, FULL_RANGE, 0.0F, OCEANS[1][i]);
		}
		addSurfaceBiome(OFF_COAST_POINTS, FULL_RANGE, FULL_RANGE, deepOceanContinentalness, FULL_RANGE, FULL_RANGE, 0.0F);
		addSurfaceBiome(OFF_COAST_POINTS, FULL_RANGE, FULL_RANGE, oceanContinentalness, FULL_RANGE, FULL_RANGE, 0.0F);
	}


	private static void addInlandBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer) {
		addMidSlice(consumer, Climate.Parameter.span(-1.0F, -0.93333334F));
		addHighSlice(consumer, Climate.Parameter.span(-0.93333334F, -0.7666667F));
		addPeaks(consumer, Climate.Parameter.span(-0.7666667F, -0.56666666F));
		addHighSlice(consumer, Climate.Parameter.span(-0.56666666F, -0.4F));
		addMidSlice(consumer, Climate.Parameter.span(-0.4F, -0.26666668F));
		addLowSlice(consumer, Climate.Parameter.span(-0.26666668F, -0.05F));
		addValleys(consumer, Climate.Parameter.span(-0.05F, 0.05F));
		addLowSlice(consumer, Climate.Parameter.span(0.05F, 0.26666668F));
		addMidSlice(consumer, Climate.Parameter.span(0.26666668F, 0.4F));
		addHighSlice(consumer, Climate.Parameter.span(0.4F, 0.56666666F));
		addPeaks(consumer, Climate.Parameter.span(0.56666666F, 0.7666667F));
		addHighSlice(consumer, Climate.Parameter.span(0.7666667F, 0.93333334F));
		addMidSlice(consumer, Climate.Parameter.span(0.93333334F, 1.0F));
	}

	private static List<Climate.ParameterPoint> MIDDLE_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getMiddlePoints() {
		runBiomes();
		return MIDDLE_POINTS;
	}

	private static List<Climate.ParameterPoint> MIDDLE_VARIANT_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getMiddleVariantPoints() {
		runBiomes();
		return MIDDLE_VARIANT_POINTS;
	}

	private static List<Climate.ParameterPoint> PEAK_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getPeakPoints() {
		runBiomes();
		return PEAK_POINTS;
	}

	private static List<Climate.ParameterPoint> PLATEAU_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getPlateauPoints() {
		runBiomes();
		return PLATEAU_POINTS;
	}

	private static List<Climate.ParameterPoint> SHATTERED_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getShatteredPoints() {
		runBiomes();
		return SHATTERED_POINTS;
	}

	private static List<Climate.ParameterPoint> BADLANDS_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getBadlandsPoints() {
		runBiomes();
		return BADLANDS_POINTS;
	}

	private static List<Climate.ParameterPoint> SLOPE_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getSlopePoints() {
		runBiomes();
		return SLOPE_POINTS;
	}

	private static List<Climate.ParameterPoint> WINDSWEPT_SAVANNA_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getWindsweptSavannaPoints() {
		runBiomes();
		return WINDSWEPT_SAVANNA_POINTS;
	}

	private static List<Climate.ParameterPoint> BEACH_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getBeachPoints() {
		runBiomes();
		return BEACH_POINTS;
	}

	private static List<Climate.ParameterPoint> SHATTERED_COAST_POINTS = new ArrayList<>();
	public static List<Climate.ParameterPoint> getShatteredCoastPoints() {
		runBiomes();
		return SHATTERED_COAST_POINTS;
	}

	private static void addPeaks(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter param) {
		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];

			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				ResourceLocation middle = pickMiddleBiome(i, j, param);
				ResourceLocation middleOrBadlands = pickMiddleBiomeOrBadlandsIfHot(i, j, param);
				ResourceLocation middleOrBadlandsOrSlope = pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, param);
				ResourceLocation plateau = pickPlateauBiome(i, j, param);
				ResourceLocation shattered = pickShatteredBiome(i, j, param);
				ResourceLocation windsweptSavannaOrShattered = maybePickWindsweptSavannaBiome(i, j, param, shattered);
				ResourceLocation peak = pickPeakBiome(i, j, param);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[0], param, 0.0F, peak);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[1], param, 0.0F, middleOrBadlandsOrSlope);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[1], param, 0.0F, peak);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[2], param, 0.0F, plateau);
				addSurfaceBiome(consumer, parameter, parameter2, midInlandContinentalness, erosions[3], param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, farInlandContinentalness, erosions[3], param, 0.0F, plateau);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[5], param, 0.0F, windsweptSavannaOrShattered);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F, shattered);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, middle);
			}
		}

		//MIDDLE
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[6], param, 0.0F);
		//PEAK
		addSurfaceBiome(PEAK_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[0], param, 0.0F);
		addSurfaceBiome(PEAK_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[1], param, 0.0F);
		//PLATEAU
		addSurfaceBiome(PLATEAU_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[2], param, 0.0F);
		addSurfaceBiome(PLATEAU_POINTS, FULL_RANGE, FULL_RANGE, farInlandContinentalness, erosions[3], param, 0.0F);
		//SHATTERED
		addSurfaceBiome(SHATTERED_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F);

		for(int t = 0; t < temperatures.length; ++t) {
			Climate.Parameter temperature = temperatures[t];
			for (int h = 0; h < humidities.length; ++h) {
				Climate.Parameter humidity = humidities[h];
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(t, h), temperature, humidity, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[1], param, 0.0F);
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHot(t, h), temperature, humidity, midInlandContinentalness, erosions[3], param, 0.0F);
				addSurfaceBiome(maybePickWindsweptSavannaBiome(t, h, param, SHATTERED_POINTS), temperature, humidity, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[5], param, 0.0F);
			}
		}
	}

	private static void addHighSlice(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter param) {
		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];

			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				ResourceLocation middle = pickMiddleBiome(i, j, param);
				ResourceLocation middleOrBadlands = pickMiddleBiomeOrBadlandsIfHot(i, j, param);
				ResourceLocation middleOrBadlandsOrSlope = pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, param);
				ResourceLocation plateau = pickPlateauBiome(i, j, param);
				ResourceLocation shattered = pickShatteredBiome(i, j, param);
				ResourceLocation windsweptSavannaOrMiddle = maybePickWindsweptSavannaBiome(i, j, param, middle);
				ResourceLocation slope = pickSlopeBiome(i, j, param);
				ResourceLocation peak = pickPeakBiome(i, j, param);
				addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[0], param, 0.0F, slope);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[0], param, 0.0F, peak);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[1], param, 0.0F, middleOrBadlandsOrSlope);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[1], param, 0.0F, slope);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[2], param, 0.0F, plateau);
				addSurfaceBiome(consumer, parameter, parameter2, midInlandContinentalness, erosions[3], param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, farInlandContinentalness, erosions[3], param, 0.0F, plateau);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[5], param, 0.0F, windsweptSavannaOrMiddle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F, shattered);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, middle);
			}
		}

		//MIDDLE
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[6], param, 0.0F);
		//SLOPE
		addSurfaceBiome(SLOPE_POINTS, FULL_RANGE, FULL_RANGE, nearInlandContinentalness, erosions[0], param, 0.0F);
		addSurfaceBiome(SLOPE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[1], param, 0.0F);
		//PEAK
		addSurfaceBiome(PEAK_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[0], param, 0.0F);
		//PLATEAU
		addSurfaceBiome(PLATEAU_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[2], param, 0.0F);
		addSurfaceBiome(PLATEAU_POINTS, FULL_RANGE, FULL_RANGE, farInlandContinentalness, erosions[3], param, 0.0F);
		//SHATTERED
		addSurfaceBiome(SHATTERED_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F);

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter temperature = temperatures[i];
			for (int j = 0; j < humidities.length; ++j) {
				Climate.Parameter humidity = humidities[j];
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j), temperature, humidity, nearInlandContinentalness, erosions[1], param, 0.0F);
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHot(i, j), temperature, humidity, midInlandContinentalness, erosions[3], param, 0.0F);
				addSurfaceBiome(maybePickWindsweptSavannaBiome(i, j, param, MIDDLE_POINTS), temperature, humidity, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[5], param, 0.0F);
			}
		}
	}

	private static void addMidSlice(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter param) {
		addSurfaceBiome(consumer, FULL_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[2]), param, 0.0F, Biomes.STONY_SHORE.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[1], temperatures[2]), FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.SWAMP.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[3], temperatures[4]), FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.MANGROVE_SWAMP.location());

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];

			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				ResourceLocation middle = pickMiddleBiome(i, j, param);
				ResourceLocation middleOrBadlands = pickMiddleBiomeOrBadlandsIfHot(i, j, param);
				ResourceLocation middleOrBadlandsOrSlope = pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, param);
				ResourceLocation shattered = pickShatteredBiome(i, j, param);
				ResourceLocation plateau = pickPlateauBiome(i, j, param);
				ResourceLocation beach = pickBeachBiome(i, j);
				ResourceLocation windsweptOrMiddle = maybePickWindsweptSavannaBiome(i, j, param, middle);
				ResourceLocation shatteredCoast = pickShatteredCoastBiome(i, j, param);
				ResourceLocation slope = pickSlopeBiome(i, j, param);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[0], param, 0.0F, slope);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, midInlandContinentalness), erosions[1], param, 0.0F, middleOrBadlandsOrSlope);
				addSurfaceBiome(consumer, parameter, parameter2, farInlandContinentalness, erosions[1], param, 0.0F, i == 0 ? slope : plateau);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[2], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, midInlandContinentalness, erosions[2], param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, farInlandContinentalness, erosions[2], param, 0.0F, plateau);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[3], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[3], param, 0.0F, middleOrBadlands);
				if (param.max() < 0L) {
					addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[4], param, 0.0F, beach);
					addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[4], param, 0.0F, middle);
				} else {
					addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F, middle);
				}

				addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[5], param, 0.0F, shatteredCoast);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[5], param, 0.0F, windsweptOrMiddle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F, shattered);
				if (param.max() < 0L) {
					addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[6], param, 0.0F, beach);
				} else {
					addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[6], param, 0.0F, middle);
				}

				if (i == 0) {
					addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, middle);
				}
			}
		}

		addSurfaceBiome(SLOPE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[0], param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, nearInlandContinentalness, erosions[2], param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, nearInlandContinentalness), erosions[3], param, 0.0F);
		addSurfaceBiome(PLATEAU_POINTS, FULL_RANGE, FULL_RANGE, farInlandContinentalness, erosions[2], param, 0.0F);
		addSurfaceBiome(SHATTERED_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F);
		addSurfaceBiome(SHATTERED_COAST_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[5], param, 0.0F);

		if (param.max() < 0L) {
			addSurfaceBiome(BEACH_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[4], param, 0.0F);
			addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[4], param, 0.0F);
			addSurfaceBiome(BEACH_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[6], param, 0.0F);
		} else {
			addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), erosions[4], param, 0.0F);
			addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[6], param, 0.0F);
		}

		addSurfaceBiome(SLOPE_POINTS, Temperature.ONE, FULL_RANGE, farInlandContinentalness, erosions[1], param, 0.0F);
		addSurfaceBiome(PLATEAU_POINTS, Climate.Parameter.span(Temperature.TWO, Temperature.FIVE), FULL_RANGE, farInlandContinentalness, erosions[1], param, 0.0F);
		addSurfaceBiome(MIDDLE_POINTS, Temperature.ONE, FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F);

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter temperature = temperatures[i];
			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter humidity = humidities[j];
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j), temperature, humidity, Climate.Parameter.span(nearInlandContinentalness, midInlandContinentalness), erosions[1], param, 0.0F);
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHot(i, j), temperature, humidity, midInlandContinentalness, erosions[2], param, 0.0F);
				addSurfaceBiome(pickMiddleBiomeOrBadlandsIfHot(i, j), temperature, humidity, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[3], param, 0.0F);
				addSurfaceBiome(maybePickWindsweptSavannaBiome(i, j, param, MIDDLE_POINTS), temperature, humidity, nearInlandContinentalness, erosions[5], param, 0.0F);
			}
		}
	}

	private static void addLowSlice(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter param) {
		addSurfaceBiome(consumer, FULL_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[2]), param, 0.0F, Biomes.STONY_SHORE.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[1], temperatures[2]), FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.SWAMP.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[3], temperatures[4]), FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.MANGROVE_SWAMP.location());

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];

			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				ResourceLocation middle = pickMiddleBiome(i, j, param);
				ResourceLocation middleOrBadlands = pickMiddleBiomeOrBadlandsIfHot(i, j, param);
				ResourceLocation middleOrBadlandsOrSlope = pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(i, j, param);
				ResourceLocation beach = pickBeachBiome(i, j);
				ResourceLocation windsweptSavannaOrMiddle = maybePickWindsweptSavannaBiome(i, j, param, middle);
				ResourceLocation shatteredCoast = pickShatteredCoastBiome(i, j, param);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, middleOrBadlandsOrSlope);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, Climate.Parameter.span(erosions[3], erosions[4]), param, 0.0F, beach);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[4], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[5], param, 0.0F, shatteredCoast);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[5], param, 0.0F, windsweptSavannaOrMiddle);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F, middle);
				addSurfaceBiome(consumer, parameter, parameter2, coastContinentalness, erosions[6], param, 0.0F, beach);
				if (i == 0) {
					addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, middle);
				}
			}
		}

		addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), erosions[5], param, 0.0F);
		addSurfaceBiome(BEACH_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[6], param, 0.0F);

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];
			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, middleOrBadlands);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, middleOrBadlandsOrSlope);
				addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, nearInlandContinentalness, Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[3]), param, 0.0F, middleOrBadlands);
				addSurfaceBiome(BEACH_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[3], erosions[4]), param, 0.0F);
				addSurfaceBiome(MIDDLE_POINTS, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[4], param, 0.0F);
				addSurfaceBiome(SHATTERED_COAST_POINTS, FULL_RANGE, FULL_RANGE, coastContinentalness, erosions[5], param, 0.0F);
				addSurfaceBiome(consumer, parameter, parameter2, nearInlandContinentalness, erosions[5], param, 0.0F, windsweptSavannaOrMiddle);
				if (i == 0) {
					addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(nearInlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, middle);
				}
			}
		}
	}

	private static void addValleys(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter param) {
		addSurfaceBiome(consumer, FROZEN_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, param.max() < 0L ? Biomes.STONY_SHORE.location() : Biomes.FROZEN_RIVER.location());
		addSurfaceBiome(consumer, UNFROZEN_RANGE, FULL_RANGE, coastContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, param.max() < 0L ? Biomes.STONY_SHORE.location() : Biomes.RIVER.location());
		addSurfaceBiome(consumer, FROZEN_RANGE, FULL_RANGE, nearInlandContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, Biomes.FROZEN_RIVER.location());
		addSurfaceBiome(consumer, UNFROZEN_RANGE, FULL_RANGE, nearInlandContinentalness, Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, Biomes.RIVER.location());
		addSurfaceBiome(consumer, FROZEN_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[5]), param, 0.0F, Biomes.FROZEN_RIVER.location());
		addSurfaceBiome(consumer, UNFROZEN_RANGE, FULL_RANGE, Climate.Parameter.span(coastContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[2], erosions[5]), param, 0.0F, Biomes.RIVER.location());
		addSurfaceBiome(consumer, FROZEN_RANGE, FULL_RANGE, coastContinentalness, erosions[6], param, 0.0F, Biomes.FROZEN_RIVER.location());
		addSurfaceBiome(consumer, UNFROZEN_RANGE, FULL_RANGE, coastContinentalness, erosions[6], param, 0.0F, Biomes.RIVER.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[1], temperatures[2]), FULL_RANGE, Climate.Parameter.span(inlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.SWAMP.location());
		addSurfaceBiome(consumer, Climate.Parameter.span(temperatures[3], temperatures[4]), FULL_RANGE, Climate.Parameter.span(inlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.MANGROVE_SWAMP.location());
		addSurfaceBiome(consumer, FROZEN_RANGE, FULL_RANGE, Climate.Parameter.span(inlandContinentalness, farInlandContinentalness), erosions[6], param, 0.0F, Biomes.FROZEN_RIVER.location());

		for(int i = 0; i < temperatures.length; ++i) {
			Climate.Parameter parameter = temperatures[i];

			for(int j = 0; j < humidities.length; ++j) {
				Climate.Parameter parameter2 = humidities[j];
				ResourceLocation ResourceLocation = pickMiddleBiomeOrBadlandsIfHot(i, j, param);
				addSurfaceBiome(consumer, parameter, parameter2, Climate.Parameter.span(midInlandContinentalness, farInlandContinentalness), Climate.Parameter.span(erosions[0], erosions[1]), param, 0.0F, ResourceLocation);
			}
		}

	}

	private static void addUndergroundBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consume) {
		addUndergroundBiome(consume, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(0.8F, 1.0F), FULL_RANGE, FULL_RANGE, 0.0F, Biomes.DRIPSTONE_CAVES.location());
		addUndergroundBiome(consume, FULL_RANGE, Climate.Parameter.span(0.7F, 1.0F), FULL_RANGE, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.LUSH_CAVES.location());
		addBottomBiome(consume, FULL_RANGE, FULL_RANGE, FULL_RANGE, Climate.Parameter.span(erosions[0], erosions[1]), FULL_RANGE, 0.0F, Biomes.DEEP_DARK.location());
	}

	private static ResourceLocation pickMiddleBiome(int temperature, int humidity, Climate.Parameter param) {
		if (param.max() < 0L) {
			return MIDDLE_BIOMES[temperature][humidity];
		} else {
			ResourceLocation ResourceLocation = MIDDLE_BIOMES_VARIANT[temperature][humidity];
			return ResourceLocation == null ? MIDDLE_BIOMES[temperature][humidity] : ResourceLocation;
		}
	}

	private static ResourceLocation pickMiddleBiomeOrBadlandsIfHot(int temperature, int humidity, Climate.Parameter param) {
		return temperature == 4 ? pickBadlandsBiome(humidity, param) : pickMiddleBiome(temperature, humidity, param);
	}

	private static ResourceLocation pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int temperature, int humidity, Climate.Parameter param) {
		return temperature == 0 ? pickSlopeBiome(temperature, humidity, param) : pickMiddleBiomeOrBadlandsIfHot(temperature, humidity, param);
	}

	private static ResourceLocation maybePickWindsweptSavannaBiome(int temperature, int humidity, Climate.Parameter param, ResourceLocation key) {
		return temperature > 1 && humidity < 4 && param.max() >= 0L ? Biomes.WINDSWEPT_SAVANNA.location() : key;
	}

	private static ResourceLocation pickShatteredCoastBiome(int temperature, int humidity, Climate.Parameter param) {
		ResourceLocation ResourceLocation = param.max() >= 0L ? pickMiddleBiome(temperature, humidity, param) : pickBeachBiome(temperature, humidity);
		return maybePickWindsweptSavannaBiome(temperature, humidity, param, ResourceLocation);
	}

	private static ResourceLocation pickBeachBiome(int temperature, int humidity) {
		if (temperature == 0) {
			return Biomes.SNOWY_BEACH.location();
		} else {
			return temperature == 4 ? Biomes.DESERT.location() : Biomes.BEACH.location();
		}
	}

	private static ResourceLocation pickBadlandsBiome(int humidity, Climate.Parameter param) {
		if (humidity < 2) {
			return param.max() < 0L ? Biomes.BADLANDS.location() : Biomes.ERODED_BADLANDS.location();
		} else {
			return humidity < 3 ? Biomes.BADLANDS.location() : Biomes.WOODED_BADLANDS.location();
		}
	}

	private static ResourceLocation pickPlateauBiome(int temperature, int humidity, Climate.Parameter param) {
		if (param.max() < 0L) {
			return PLATEAU_BIOMES[temperature][humidity];
		} else {
			ResourceLocation ResourceLocation = PLATEAU_BIOMES_VARIANT[temperature][humidity];
			return ResourceLocation == null ? PLATEAU_BIOMES[temperature][humidity] : ResourceLocation;
		}
	}

	private static ResourceLocation pickPeakBiome(int temperature, int humidity, Climate.Parameter param) {
		if (temperature <= 2) {
			return param.max() < 0L ? Biomes.JAGGED_PEAKS.location() : Biomes.FROZEN_PEAKS.location();
		} else {
			return temperature == 3 ? Biomes.STONY_PEAKS.location() : pickBadlandsBiome(humidity, param);
		}
	}

	private static ResourceLocation pickSlopeBiome(int temperature, int humidity, Climate.Parameter param) {
		if (temperature >= 3) {
			return pickPlateauBiome(temperature, humidity, param);
		} else {
			return humidity <= 1 ? Biomes.SNOWY_SLOPES.location() : Biomes.GROVE.location();
		}
	}

	private static ResourceLocation pickShatteredBiome(int temperature, int humidity, Climate.Parameter param) {
		ResourceLocation ResourceLocation = SHATTERED_BIOMES[temperature][humidity];
		return ResourceLocation == null ? pickMiddleBiome(temperature, humidity, param) : ResourceLocation;
	}

	private static void addSurfaceBiome(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness, ResourceLocation key) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), depth, weirdness), key));
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), depth, weirdness), key));
	}

	private static void addUndergroundBiome(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness, ResourceLocation key) {
		consumer.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.2F, 0.9F), depth, weirdness), key));
	}

	private static void addBottomBiome(Consumer<Pair<Climate.ParameterPoint, ResourceLocation>> consumer, Climate.Parameter parameter, Climate.Parameter parameter2, Climate.Parameter parameter3, Climate.Parameter parameter4, Climate.Parameter parameter5, float f, ResourceLocation registryKey) {
		consumer.accept(Pair.of(Climate.parameters(parameter, parameter2, parameter3, parameter4, Climate.Parameter.point(1.1F), parameter5, f), registryKey));
	}

	private static void addSurfaceBiome(List<Climate.ParameterPoint> list, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness) {
		list.add(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), depth, weirdness));
		list.add(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), depth, weirdness));
	}

	private static void addUndergroundBiome(List<Climate.ParameterPoint> list, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, float weirdness) {
		list.add(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.2F, 0.9F), depth, weirdness));
	}

	private static void addBottomBiome(List<Climate.ParameterPoint> list, Climate.Parameter parameter, Climate.Parameter parameter2, Climate.Parameter parameter3, Climate.Parameter parameter4, Climate.Parameter parameter5, float f) {
		list.add(Climate.parameters(parameter, parameter2, parameter3, parameter4, Climate.Parameter.point(1.1F), parameter5, f));
	}

	//LISTS
	private static List<Climate.ParameterPoint> pickMiddleBiomeOrBadlandsIfHot(int temperature, int humidity) {
		return temperature == 4 ? BADLANDS_POINTS : MIDDLE_POINTS;
	}

	private static List<Climate.ParameterPoint> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int temperature, int humidity) {
		return temperature == 0 ? SLOPE_POINTS : pickMiddleBiomeOrBadlandsIfHot(temperature, humidity);
	}

	private static List<Climate.ParameterPoint> maybePickWindsweptSavannaBiome(int temperature, int humidity, Climate.Parameter param, List<Climate.ParameterPoint> list) {
		return temperature > 1 && humidity < 4 && param.max() >= 0L ? WINDSWEPT_SAVANNA_POINTS : list;
	}

}
