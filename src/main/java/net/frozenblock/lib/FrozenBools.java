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
	public static final boolean IS_FABRIC = FabricLoader.getInstance().isModLoaded("fabric-api")
			&& !(FabricLoader.getInstance().isModLoaded("qsl")
			|| FabricLoader.getInstance().isModLoaded("quilted_fabric_api"));
	public static final boolean IS_QUILT = FabricLoader.getInstance().isModLoaded("qsl")
			|| FabricLoader.getInstance().isModLoaded("quilted_fabric_api");
	public static final boolean IS_FORGE = FabricLoader.getInstance().isModLoaded("connector");

    // EXTERNAL MODS
	public static final boolean HAS_ARCHITECTURY = FabricLoader.getInstance().isModLoaded("architectury");
	public static final boolean HAS_BCLIB = FabricLoader.getInstance().isModLoaded("bclib");
	public static final boolean HAS_C2ME = FabricLoader.getInstance().isModLoaded("c2me");
    public static final boolean HAS_CLOTH_CONFIG = FabricLoader.getInstance().isModLoaded("cloth-config");
	public static final boolean HAS_CONTINUITY = FabricLoader.getInstance().isModLoaded("continuity");
	public static final boolean HAS_ENTITY_CULLING = FabricLoader.getInstance().isModLoaded("entityculling");
	public static final boolean HAS_EVERY_COMPAT = FabricLoader.getInstance().isModLoaded("everycomp");
	public static final boolean HAS_GECKO_LIB = FabricLoader.getInstance().isModLoaded("geckolib3");
	public static final boolean HAS_INDIUM = FabricLoader.getInstance().isModLoaded("indium");
	public static final boolean HAS_IRIS = FabricLoader.getInstance().isModLoaded("iris");
	public static final boolean HAS_MOONLIGHT_LIB = FabricLoader.getInstance().isModLoaded("moonlight");
    public static final boolean HAS_SIMPLE_COPPER_PIPES = FabricLoader.getInstance().isModLoaded("copper_pipe");
	public static final boolean HAS_ROUGHLY_ENOUGH_ITEMS = FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
	public static final boolean HAS_ROUGHLY_ENOUGH_RESOURCES = FabricLoader.getInstance().isModLoaded("roughlyenoughresources");
	public static final boolean HAS_REPURPOSED_STRUCTURES = FabricLoader.getInstance().isModLoaded("repurposed_structures");
	public static final boolean HAS_SERVER_CORE = FabricLoader.getInstance().isModLoaded("servercore");
    public static final boolean HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
	public static final boolean HAS_STARLIGHT = FabricLoader.getInstance().isModLoaded("starlight");
    public static final boolean HAS_TERRABLENDER = FabricLoader.getInstance().isModLoaded("terrablender");
    public static final boolean HAS_TERRALITH = FabricLoader.getInstance().isModLoaded("terralith");
}
