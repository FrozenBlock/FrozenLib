/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.worldgen.feature.api;

import net.minecraft.data.worldgen.BootstrapContext;

public final class FrozenFeatureUtils {
	private FrozenFeatureUtils() {
		throw new UnsupportedOperationException("FrozenFeatureUtils contains only static declarations.");
	}

	public static BootstrapContext<Object> BOOTSTRAP_CONTEXT = null;
}
