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

import net.minecraft.world.level.biome.Climate;
import java.util.List;

public final class FrozenBiomeParameters {
	private FrozenBiomeParameters() {
		throw new UnsupportedOperationException("FrozenBiomeParameters contains only static declarations.");
	}

	public static void addWeirdness(BiomeRunnable runnable, List<Climate.Parameter> weirdnesses) {
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
	public static Climate.Parameter inBetween(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.25F;
		return Climate.Parameter.span(lowest + offsetBy, highest - offsetBy);
	}

	/**
	 * Returns climate parameters in between both specified parameters.
	 * <p>
	 * Is NOT identical to Climate.Parameter;span.
	 * Instead, this will meet in the middle of both parameters.
	 */
	public static Climate.Parameter inBetweenTighter(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.375F;
		return Climate.Parameter.span(lowest + offsetBy, highest - offsetBy);
	}

	public static Climate.Parameter inBetweenLower(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.25F;
		return Climate.Parameter.span(lowest, par1.max() - offsetBy);
	}

	public static Climate.Parameter inBetweenTighterLower(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.125F;
		return Climate.Parameter.span(lowest, par1.max() - offsetBy);
	}

	public static Climate.Parameter inBetweenLowerHigher(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.25F;
		return Climate.Parameter.span(par2.min(), highest - offsetBy);
	}

	public static Climate.Parameter inBetweenTighterHigher(Climate.Parameter par1, Climate.Parameter par2) {
		float lowest = par1.min();
		float highest = par2.max();
		if (lowest > highest) {
			throw new UnsupportedOperationException("FrozenLib: Cannot run inBetween when lower parameter is higher than the first!");
		}
		float offsetBy = (highest - lowest) * 0.125F;
		return Climate.Parameter.span(par2.min(), highest - offsetBy);
	}

	@FunctionalInterface
	public interface BiomeRunnable {
		void run(Climate.Parameter weirdness);
	}
}
