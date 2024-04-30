/*
 * Copyright 2023 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.worldgen.biome.api.parameters;

import java.util.List;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class FrozenBiomeParameters {
	private FrozenBiomeParameters() {
		throw new UnsupportedOperationException("FrozenBiomeParameters contains only static declarations.");
	}

	public static void addWeirdness(BiomeRunnable runnable, @NotNull List<Climate.Parameter> weirdnesses) {
		for (Climate.Parameter weirdness : weirdnesses) {
			runnable.run(weirdness);
		}
	}

	/**
	 * Returns climate parameters in between both specified parameters.
	 * <p>
	 * Is NOT identical to Climate.Parameter;span.
	 * Instead, this will meet in the middle of both parameters.
	 */
	@NotNull
	public static Climate.Parameter inBetween(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween if width >= 1 or width <= 0!");
		}
		width *= 0.5F;
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float difference = (highest - lowest);
		float middle = lowest + (difference * 0.5F);
		float offset = difference * width;
		return Climate.Parameter.span(middle - offset, middle + offset);
	}

	@NotNull
	public static Climate.Parameter inBetweenLowCutoff(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetweenLowCutoff if width >= 1 or width <= 0!");
		}
		width *= 0.5F;
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetweenLowCutoff when lower parameter is higher than the first!");
		}
		float difference = (highest - lowest);
		float middle = lowest + (difference * 0.5F);
		float offset = difference * width;
		return Climate.Parameter.span(lowest, middle - offset);
	}

	@NotNull
	public static Climate.Parameter inBetweenHighCutoff(Climate.Parameter par1, Climate.Parameter par2, float width) {
		if (width >= 1F || width <= 0F) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetweenHighCutoff if width >= 1 or width <= 0!");
		}
		width *= 0.5F;
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetweenHighCutoff when lower parameter is higher than the first!");
		}
		float difference = (highest - lowest);
		float middle = lowest + (difference * 0.5F);
		float offset = difference * width;
		return Climate.Parameter.span(middle + offset, highest);
	}

	@NotNull
	@Contract("_, _ -> new")
	public static Climate.Parameter squish(@NotNull Climate.Parameter parameter, float squish) {
		return Climate.Parameter.span(parameter.min() + squish, parameter.max() - squish);
	}

	public static boolean isWeird(@NotNull Climate.ParameterPoint point) {
		return point.weirdness().max() < 0L;
	}

	@FunctionalInterface
	public interface BiomeRunnable {
		void run(Climate.Parameter weirdness);
	}
}
