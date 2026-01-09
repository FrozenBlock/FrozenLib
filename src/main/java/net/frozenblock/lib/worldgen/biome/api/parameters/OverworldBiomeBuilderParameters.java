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

package net.frozenblock.lib.worldgen.biome.api.parameters;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.worldgen.biome.impl.BiomeParameters;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class OverworldBiomeBuilderParameters {
	public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1F, 1F);
	public static final Map<Identifier, BiomeParameters> BIOMES = new LinkedHashMap<>();

	private static boolean hasRun = false;

	public static BiomeParameters getParameters(Identifier location) {
		runBiomes();
		return getOrCreateParameters(location);
	}

	private static void runBiomes() {
		if (hasRun) return;
		hasRun = true;
		addBiomes(pair -> addParameters(pair.getFirst(), pair.getSecond()));
	}

	public static BiomeParameters getParameters(ResourceKey<Biome> key) {
		return getParameters(key.identifier());
	}

	private static void addParameters(Identifier biome, Climate.ParameterPoint parameters) {
		final BiomeParameters biomeParameters = getOrCreateParameters(biome);
		biomeParameters.add(parameters);
	}

	private static BiomeParameters getOrCreateParameters(Identifier biome) {
		if (BIOMES.containsKey(biome)) return BIOMES.get(biome);
		final BiomeParameters parameters = new BiomeParameters();
		BIOMES.put(biome, parameters);
		return parameters;
	}

	@Contract(pure = true)
	public static List<Climate.ParameterPoint> points(BiomeParameters parameters) {
		return parameters.points;
	}

	public static List<Climate.ParameterPoint> points(ResourceKey<Biome> key) {
		return points(key.identifier());
	}

	public static List<Climate.ParameterPoint> points(Identifier biome) {
		return points(OverworldBiomeBuilderParameters.getParameters(biome));
	}

	private static void addBiomes(Consumer<Pair<Identifier, Climate.ParameterPoint>> key) {
		final ImmutableList.Builder<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> builder = new ImmutableList.Builder<>();
		new OverworldBiomeBuilder().addBiomes(parameterPointResourceKeyPair -> builder.add(parameterPointResourceKeyPair));
		builder.build().forEach(parameterPointResourceKeyPair -> key.accept(new Pair<>(parameterPointResourceKeyPair.getSecond().identifier(), parameterPointResourceKeyPair.getFirst())));
	}

	private static final List<Climate.ParameterPoint> OFF_COAST_POINTS = new ArrayList<>();

	public static List<Climate.ParameterPoint> getOffCoastPoints() {
		runBiomes();
		return OFF_COAST_POINTS;
	}

}
