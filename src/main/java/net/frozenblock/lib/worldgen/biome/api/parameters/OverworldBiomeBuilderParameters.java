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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

@UtilityClass
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
	public static final Climate.Parameter[] TEMPERATURES = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.45F),
			Climate.Parameter.span(-0.45F, -0.15F),
			Climate.Parameter.span(-0.15F, 0.2F),
			Climate.Parameter.span(0.2F, 0.55F),
			Climate.Parameter.span(0.55F, 1.0F)
	};
	public static final Climate.Parameter[] HUMIDITIES = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.35F),
			Climate.Parameter.span(-0.35F, -0.1F),
			Climate.Parameter.span(-0.1F, 0.1F),
			Climate.Parameter.span(0.1F, 0.3F),
			Climate.Parameter.span(0.3F, 1.0F)
	};
	public static final Climate.Parameter[] EROSIONS = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.78F),
			Climate.Parameter.span(-0.78F, -0.375F),
			Climate.Parameter.span(-0.375F, -0.2225F),
			Climate.Parameter.span(-0.2225F, 0.05F),
			Climate.Parameter.span(0.05F, 0.45F),
			Climate.Parameter.span(0.45F, 0.55F),
			Climate.Parameter.span(0.55F, 1.0F)
	};
	public static final Climate.Parameter FROZEN_RANGE = TEMPERATURES[0];
	public static final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(TEMPERATURES[1], TEMPERATURES[4]);
	public static final Climate.Parameter MUSHROOM_FIELDS_CONTINENTALNESS = Climate.Parameter.span(-1.2F, -1.05F);
	public static final Climate.Parameter DEEP_OCEAN_CONTINENTALNESS = Climate.Parameter.span(-1.05F, -0.455F);
	public static final Climate.Parameter OCEAN_CONTINENTALNESS = Climate.Parameter.span(-0.455F, -0.19F);
	public static final Climate.Parameter COAST_CONTINENTALNESS = Climate.Parameter.span(-0.19F, -0.11F);
	public static final Climate.Parameter INLAND_CONTINENTALNESS = Climate.Parameter.span(-0.11F, 0.55F);
	public static final Climate.Parameter NEAR_INLAND_CONTINENTALNESS = Climate.Parameter.span(-0.11F, 0.03F);
	public static final Climate.Parameter MID_INLAND_CONTINENTALNESS = Climate.Parameter.span(0.03F, 0.3F);
	public static final Climate.Parameter FAR_INLAND_CONTINENTALNESS = Climate.Parameter.span(0.3F, 1.0F);
	public static final Map<ResourceLocation, BiomeParameters> BIOMES = new LinkedHashMap<>();
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

	private static void addParameters(ResourceLocation location, Climate.ParameterPoint parameters) {
		BiomeParameters biomeParameters = getOrCreateParameters(location);
		biomeParameters.add(parameters);
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

	private static void addBiomes(Consumer<Pair<ResourceLocation, Climate.ParameterPoint>> key) {
		ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder<>();
		new OverworldBiomeBuilder().addBiomes(parameterPointResourceKeyPair -> builder.add(parameterPointResourceKeyPair));
		builder.build().forEach(parameterPointResourceKeyPair -> key.accept(new Pair<>(parameterPointResourceKeyPair.getSecond().location(), parameterPointResourceKeyPair.getFirst())));
	}

	private static final List<Climate.ParameterPoint> OFF_COAST_POINTS = new ArrayList<>();

	public static List<Climate.ParameterPoint> getOffCoastPoints() {
		runBiomes();
		return OFF_COAST_POINTS;
	}

}
