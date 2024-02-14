/*
 * Copyright 2023-2024 FrozenBlock
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

package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.Bootstrap;

public class FrozenBools {

	/**
	 * This is set to true when {@link Bootstrap#bootStrap()} is finished.
	 */
	public static boolean isInitialized;

    public static boolean useNewDripstoneLiquid = false;

	// MOD LOADERS
	public static final boolean IS_FABRIC = hasMod("fabricloader") && !hasMod("quilt_loader")
	public static final boolean IS_QUILT = hasMod("quilt_loader");
	public static final boolean IS_FORGE = hasMod("connector");

    // EXTERNAL MODS
	public static final boolean HAS_ARCHITECTURY = hasMod("architectury");
	public static final boolean HAS_C2ME = hasMod("c2me");
    public static final boolean HAS_CLOTH_CONFIG = hasMod("cloth-config");
	public static final boolean HAS_INDIUM = hasMod("indium");
	public static final boolean HAS_IRIS = hasMod("iris");
	public static final boolean HAS_MOONLIGHT_LIB = hasMod("moonlight");
    public static final boolean HAS_SIMPLE_COPPER_PIPES = hasMod("copper_pipe");
    public static final boolean HAS_SODIUM = hasMod("sodium");
	public static final boolean HAS_STARLIGHT = hasMod("starlight");
    public static final boolean HAS_TERRABLENDER = hasMod("terrablender");
    public static final boolean HAS_TERRALITH = hasMod("terralith");

	public static boolean hasMod(String mod) {
		return FabricLoader.getInstance().isModLoaded(mod);
	}
}
