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

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class FrozenBiomeParameters {

	public static void addWeirdness(BiomeRunnable runnable, List<Climate.Parameter> weirdnesses) {
		for (Climate.Parameter weirdness : weirdnesses) runnable.run(weirdness);
	}

	/**
	 * Returns climate parameters in between both specified parameters.
	 * <p>
	 * Is NOT identical to Climate.Parameter;span.
	 * Instead, this will meet in the middle of both parameters.
	 */
	public static Climate.Parameter inBetween(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) throw new IllegalArgumentException("FrozenLib: Cannot run inBetween if width >= 1 or width <= 0!");

		width *= 0.5F;
		final float lowest = par1.min();
		final float highest = par2.max();
		if (lowest > highest) throw new IllegalArgumentException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");

		final float difference = (highest - lowest);
		final float middle = lowest + (difference * 0.5F);
		final float offset = difference * width;
		return Climate.Parameter.span(middle - offset, middle + offset);
	}

	public static Climate.Parameter inBetweenLowCutoff(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) throw new IllegalArgumentException("FrozenLib: Cannot run inBetweenLowCutoff if width >= 1 or width <= 0!");

		width *= 0.5F;
		final float lowest = par1.min();
		final float highest = par2.max();
		if (lowest > highest) throw new IllegalArgumentException("FrozenLib: Cannot run inBetweenLowCutoff when lower parameter is higher than the first!");

		final float difference = (highest - lowest);
		final float middle = lowest + (difference * 0.5F);
		final float offset = difference * width;
		return Climate.Parameter.span(lowest, middle - offset);
	}

	public static Climate.Parameter inBetweenHighCutoff(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) throw new IllegalArgumentException("FrozenLib: Cannot run inBetweenHighCutoff if width >= 1 or width <= 0!");

		width *= 0.5F;
		final float lowest = par1.min();
		final float highest = par2.max();
		if (lowest > highest) throw new IllegalArgumentException("FrozenLib: Cannot run inBetweenHighCutoff when lower parameter is higher than the first!");

		final float difference = (highest - lowest);
		final float middle = lowest + (difference * 0.5F);
		final float offset = difference * width;
		return Climate.Parameter.span(middle + offset, highest);
	}

	/**
	 * Returns parameters for a border between two consecutive {@link Climate.Parameter}s, with the amount of space taken up per-parameter dictated by its span.
	 * <p>
	 * The first parameter's max point must be equal to the second parameter's min point.
	 *
	 * @param firstParameter The first {@link Climate.Parameter}.
	 * @param secondParameter The second {@link Climate.Parameter}.
	 * @param percentagePerSlot The percentage of space taken up per-parameter based on its span.
	 * @return a border between two consecutive {@link Climate.Parameter}s, with the amount of space taken up per-parameter dictated by its span.
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static Climate.Parameter makeParameterBorder(Climate.Parameter firstParameter, Climate.Parameter secondParameter, float percentagePerSlot) {
		final long border = firstParameter.max();
		final long secondMin = secondParameter.min();
		if (border != secondMin) throw new IllegalArgumentException("FrozenLib: Cannot run makeParameterBorder when firstParameter's max is not equal to secondParameter's min!");

		final long firstWidth = firstParameter.max() - firstParameter.min();
		final long secondWidth = secondParameter.max() - secondParameter.min();
		return new Climate.Parameter((long) (border - (firstWidth * percentagePerSlot)), (long) (border + (secondWidth * percentagePerSlot)));
	}

	/**
	 * Finds borders between two {@link net.minecraft.world.level.biome.Climate.ParameterPoint}s, ignoring {@link Depth} and {@link Weirdness}.
	 *
	 * @param point1 The first {@link net.minecraft.world.level.biome.Climate.ParameterPoint} to find borders between.
	 * @param point2 The second {@link net.minecraft.world.level.biome.Climate.ParameterPoint} to find borders between.
	 * @param percentagePerSlot The percentage per "slot" of worldgen noise the border should take up.
	 * @return A {@link List} of found borders, ignoring {@link Depth} and {@link Weirdness}.
	 */
	public static List<Climate.ParameterPoint> findBorderParameters(Climate.ParameterPoint point1, Climate.ParameterPoint point2, float percentagePerSlot) {
		final List<Climate.ParameterPoint> borders = new ArrayList<>();

		final List<Climate.Parameter> temperatures = findBorderParameters(point1.temperature(), point2.temperature(), percentagePerSlot);
		if (temperatures.isEmpty()) return borders;

		final List<Climate.Parameter> humidities = findBorderParameters(point1.humidity(), point2.humidity(), percentagePerSlot);
		if (humidities.isEmpty()) return borders;

		final List<Climate.Parameter> continentalnesses = findBorderParameters(point1.continentalness(), point2.continentalness(), percentagePerSlot);
		if (continentalnesses.isEmpty()) return borders;

		final List<Climate.Parameter> erosions = findBorderParameters(point1.erosion(), point2.erosion(), percentagePerSlot);
		if (erosions.isEmpty()) return borders;

		final long offset = (long) ((point1.offset() + point2.offset()) * 0.5D);
		temperatures.forEach(temperature ->
			humidities.forEach(humidity ->
				continentalnesses.forEach(continentalness ->
					erosions.forEach(erosion ->
						borders.add(
							new Climate.ParameterPoint(
								temperature,
								humidity,
								continentalness,
								erosion,
								OverworldBiomeBuilderParameters.FULL_RANGE,
								OverworldBiomeBuilderParameters.FULL_RANGE,
								offset
							)
						)
					)
				)
			)
		);

		return borders;
	}

	/**
	 * Finds borders between two {@link List}s of {@link net.minecraft.world.level.biome.Climate.ParameterPoint}s, ignoring {@link Depth} and {@link Weirdness}.
	 * <p>
	 * This is best used alongside {@link OverworldBiomeBuilderParameters#getParameters(net.minecraft.resources.ResourceKey)}
	 *
	 * @param pointList1 The first {@link List} of {@link net.minecraft.world.level.biome.Climate.ParameterPoint}s to find borders between.
	 * @param pointList2 The second {@link List} of {@link net.minecraft.world.level.biome.Climate.ParameterPoint}s to find borders between.
	 * @param percentagePerSlot The percentage per "slot" of worldgen noise the border should take up.
	 * @return A {@link List} of found borders, ignoring {@link Depth} and {@link Weirdness}.
	 */
	public static List<Climate.ParameterPoint> findBorderParameters(List<Climate.ParameterPoint> pointList1, List<Climate.ParameterPoint> pointList2, float percentagePerSlot) {
		final List<Climate.ParameterPoint> borders = new ArrayList<>();
		pointList1.forEach(point1 -> pointList2.forEach(point2 -> borders.addAll(findBorderParameters(point1, point2, percentagePerSlot))));
		return borders;
	}

	/**
	 * Finds borders between two {@link net.minecraft.world.level.biome.Climate.Parameter}s.
	 * <p>
	 * Do note that this keeps intersections between {@link net.minecraft.world.level.biome.Climate.Parameter}s intact.
	 *
	 * @param firstParameter The first {@link net.minecraft.world.level.biome.Climate.Parameter} to find a border between.
	 * @param secondParameter The second {@link net.minecraft.world.level.biome.Climate.Parameter} to find a border between.
	 * @param percentagePerSlot The percentage per "slot" of worldgen noise the border should take up.
	 * @return A {@link List} of found borders and intersections.
	 */
	public static List<Climate.Parameter> findBorderParameters(Climate.Parameter firstParameter, Climate.Parameter secondParameter, float percentagePerSlot) {
		final List<Climate.Parameter> borders = new ArrayList<>();
		if (firstParameter.equals(secondParameter)) return List.of(firstParameter);

		final List<Climate.Parameter> splitFirstParam = splitParameter(firstParameter, secondParameter);
		final List<Climate.Parameter> splitSecondParam = splitParameter(secondParameter, firstParameter);

		splitFirstParam.forEach(parameter1 ->
			splitSecondParam.forEach(parameter2 -> {
				if (parameter1.equals(parameter2) && !borders.contains(parameter1)) {
					borders.add(parameter1);
					return;
				}

				try {
					final Climate.Parameter borderParameter = makeParameterBorder(parameter1, parameter2, percentagePerSlot);
					if (!borders.contains(borderParameter)) borders.add(borderParameter);
				} catch (IllegalArgumentException ignored) {}

				try {
					final Climate.Parameter borderParameter = makeParameterBorder(parameter2, parameter1, percentagePerSlot);
					if (!borders.contains(borderParameter)) borders.add(borderParameter);
				} catch (IllegalArgumentException ignored) {}
			})
		);

		return borders;
	}

	/**
	 * Splits a {@link net.minecraft.world.level.biome.Climate.Parameter} into multiple {@link net.minecraft.world.level.biome.Climate.Parameter}s, according to a reference.
	 * <p>
	 * Do note that this keeps intersections between {@link net.minecraft.world.level.biome.Climate.Parameter}s intact.
	 *
	 * @param parameter The first {@link net.minecraft.world.level.biome.Climate.Parameter}.
	 * @param referenceParameter The {@link net.minecraft.world.level.biome.Climate.Parameter} to reference and create borders with.
	 * @return A {@link List} of split-up {@link net.minecraft.world.level.biome.Climate.Parameter}s based on the given reference {@link net.minecraft.world.level.biome.Climate.Parameter}.
	 */
	@Contract(pure = true)
	public static List<Climate.Parameter> splitParameter(Climate.Parameter parameter, Climate.Parameter referenceParameter) {
		final List<Climate.Parameter> splitParameters = new ArrayList<>();

		final long min = parameter.min();
		final long max = parameter.max();

		final long refMin = referenceParameter.min();
		final long refMax = referenceParameter.max();

		if (min < refMin) splitParameters.add(new Climate.Parameter(min, refMin));
		if (max > refMax) splitParameters.add(new Climate.Parameter(refMax, max));
		if (min <= refMin && max >= refMax) splitParameters.add(new Climate.Parameter(refMin, refMax));

		return splitParameters;
	}

	@Contract("_, _ -> new")
	public static Climate.Parameter squish(Climate.Parameter parameter, float squish) {
		return Climate.Parameter.span(parameter.min() + squish, parameter.max() - squish);
	}

	public static boolean isWeird(Climate.ParameterPoint point) {
		return point.weirdness().max() < 0L;
	}

	@FunctionalInterface
	public interface BiomeRunnable {
		void run(Climate.Parameter weirdness);
	}
}
