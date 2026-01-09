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

package net.frozenblock.lib.recipe.api;

import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class RecipeExportNamespaceFix {
	private static Optional<String> CURRENT_GENERATING_MOD_ID = Optional.empty();

	public static void setCurrentGeneratingModId(String modId) {
		CURRENT_GENERATING_MOD_ID = Optional.of(modId);
	}

	public static Optional<String> getCurrentGeneratingModId() {
		return CURRENT_GENERATING_MOD_ID;
	}

	public static void clearCurrentGeneratingModId() {
		CURRENT_GENERATING_MOD_ID = Optional.empty();
	}
}
