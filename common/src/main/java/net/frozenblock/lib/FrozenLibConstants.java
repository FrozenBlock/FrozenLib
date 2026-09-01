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

import java.nio.file.Path;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import net.minecraft.SharedConstants;

@ApiStatus.Internal
public final class FrozenLibConstants {
	public static final String MOD_ID = FrozenLibEarlyConstants.MOD_ID;
	public static final Logger LOGGER = FrozenLibEarlyConstants.LOGGER;
	/**
	 * Used for features that may be unstable and crash in public builds.
	 * <p>
	 * It's smart to use this for at least registries.
	 */
	public static boolean UNSTABLE_LOGGING = FrozenLibEarlyConstants.UNSTABLE_LOGGING;
	public static final Path FROZENLIB_GAME_DIRECTORY = FrozenLibEarlyConstants.FROZENLIB_GAME_DIRECTORY;
	/**
	 * Whether the current instance is running in datagen mode.
	 * <p>
	 * This is set up to specifically support FrozenBlock's environment, it may not work outside our projects!
	 */
	public static final boolean IS_DATAGEN = FrozenLibEarlyConstants.IS_DATAGEN;

	// FROZENBLOCK MODS
	public static final String WILDER_WILD_MOD_ID = FrozenLibEarlyConstants.WILDER_WILD_MOD_ID;
	public static final boolean HAS_WILDER_WILD = FrozenLibEarlyConstants.HAS_WILDER_WILD;

	public static final String TRAILIER_TALES_MOD_ID = "trailertales";
	public static final boolean HAS_TRAILIER_TALES = FrozenLibEarlyConstants.HAS_TRAILIER_TALES;

	public static final String THE_COPPERIER_AGE_MOD_ID = "thecopperierage";
	public static final boolean HAS_THE_COPPERIER_AGE = FrozenLibEarlyConstants.HAS_THE_COPPERIER_AGE;

	public static final String CHAOS_HYPERCUBED_MOD_ID = "chaoshypercubed";
	public static final boolean HAS_CHAOS_HYPERCUBED = FrozenLibEarlyConstants.HAS_CHAOS_HYPERCUBED;

	public static final String NETHERIER_NETHER_MOD_ID = "netheriernether";
	public static final boolean HAS_NETHERIER_NETHER = FrozenLibEarlyConstants.HAS_NETHERIER_NETHER;

	public static final String GLOWTONE_MOD_ID = "glowtone";
	public static final boolean HAS_GLOWTONE = FrozenLibEarlyConstants.HAS_GLOWTONE;

	public static final String SPRINGIER_LIFE_MOD_ID = "springierlife";
	public static final boolean HAS_SPRINGIER_LIFE = FrozenLibEarlyConstants.HAS_SPRINGIER_LIFE;

	public static final String SIMPLE_COPPER_PIPES_MOD_ID = "simple_copper_pipes";
	public static final boolean HAS_SIMPLE_COPPER_PIPES = FrozenLibEarlyConstants.HAS_SIMPLE_COPPER_PIPES;

	// EXTERNAL MODS
	public static final boolean HAS_MODMENU = FrozenLibEarlyConstants.HAS_MODMENU;
	public static final boolean HAS_CLOTH_CONFIG = FrozenLibEarlyConstants.HAS_CLOTH_CONFIG;
	public static final boolean HAS_SODIUM = FrozenLibEarlyConstants.HAS_SODIUM;
	public static final boolean HAS_TERRABLENDER = FrozenLibEarlyConstants.HAS_TERRABLENDER;

	// DEBUG
	public static final boolean DEBUG_WIND = SharedConstants.debugFlag("FROZENLIB_WIND");
	public static final boolean DEBUG_WIND_DISTURBANCES = SharedConstants.debugFlag("FROZENLIB_WIND_DISTURBANCES");

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(FrozenLibConstants.MOD_ID, path);
	}

	public static ID config(String path) {
		return new ID(FrozenLibConstants.MOD_ID, path);
	}

	public static String string(String path) {
		return id(path).toString();
	}

	public static String safeString(String path) {
		return id(path).toString().replace(":", "_");
	}

	private FrozenLibConstants() {}
}
