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

package net.frozenblock.lib;

import java.util.Arrays;
import net.frozenblock.lib.platform.FrozenEarlyPlatformUtils;

public class FrozenBools {
	/**
	 * This is set to true when Bootstrap.bootStrap() is finished.
	 */
	public static boolean isInitialized;

	// DATAGEN
	/**
	 * Whether the current instance is running in datagen mode.
	 */
	public static final boolean IS_DATAGEN = isDatagen();

	private static boolean isDatagen() {
		return Arrays.stream(FrozenEarlyPlatformUtils.LOADER.getLaunchArgs())
			.toList()
			.stream()
			.anyMatch(string -> string.contains("datagen"));
	}

    // EXTERNAL MODS
	public static final boolean HAS_WILDERWILD = hasMod("wilderwild");
	public static final boolean HAS_TRAILIERTALES = hasMod("trailiertales");
	public static final boolean HAS_MODMENU = hasMod("modmenu");
    public static final boolean HAS_CLOTH_CONFIG = hasMod("cloth-config");
    public static final boolean HAS_SIMPLE_COPPER_PIPES = hasMod("simple_copper_pipes");
    public static final boolean HAS_SODIUM = hasMod("sodium");
    public static final boolean HAS_TERRABLENDER = hasMod("terrablender");

	public static boolean hasMod(String mod) {
		return FrozenEarlyPlatformUtils.LOADER.isModLoaded(mod);
	}
}
