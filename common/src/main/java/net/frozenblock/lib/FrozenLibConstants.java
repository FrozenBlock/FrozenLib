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
import net.frozenblock.lib.platform.ModLoader;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import net.minecraft.SharedConstants;

@ApiStatus.Internal
public final class FrozenLibConstants {
	public static final String MOD_ID = "frozenlib";
	public static final Logger LOGGER = FrozenLibLogUtils.LOGGER;
	/**
	 * Used for features that may be unstable and crash in public builds.
	 * <p>
	 * It's smart to use this for at least registries.
	 */
	public static boolean UNSTABLE_LOGGING = FrozenLibLogUtils.UNSTABLE_LOGGING;
	public static final Path FROZENLIB_GAME_DIRECTORY = ModLoader.getGameDir().resolve(MOD_ID);
	public static final String WILDER_WILD_MOD_ID = "wilderwild";
	public static final String TRAILIER_TALES_MOD_ID = "trailertales";
	public static final String THE_COPPERIER_AGE_MOD_ID = "thecopperierage";
	public static final String CHAOS_HYPERCUBED_MOD_ID = "chaoshypercubed";
	public static final String NETHERIER_NETHER_MOD_ID = "netheriernether";
	public static final String SPRINGIER_LIFE_MOD_ID = "springierlife";
	public static final String SIMPLE_COPPER_PIPES_MOD_ID = "simple_copper_pipes";

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
