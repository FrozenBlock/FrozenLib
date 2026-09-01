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
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

@ApiStatus.Internal
@UtilityClass
public final class FrozenLibEarlyConstants {
	public static final String MOD_ID = "frozenlib";
	public static final Logger LOGGER = FrozenLibLogUtils.LOGGER;
	/**
	 * Used for features that may be unstable and crash in public builds.
	 * <p>
	 * It's smart to use this for at least registries.
	 */
	public static boolean UNSTABLE_LOGGING = FrozenLibLogUtils.UNSTABLE_LOGGING;
	public static final Path FROZENLIB_GAME_DIRECTORY = ModLoader.getGameDir().resolve(MOD_ID);
	/**
	 * Whether the current instance is running in datagen mode.
	 * <p>
	 * This is set up to specifically support FrozenBlock's environment, it may not work outside our projects!
	 */
	public static final boolean IS_DATAGEN = isDatagen();

	private static boolean isDatagen() {
		final boolean isDatagen = Arrays.stream(ModLoader.getLaunchArgs())
			.toList()
			.stream()
			.anyMatch(string -> string.contains("run/data"));
		if (ModLoader.isDevelopmentEnvironment()) FrozenLibLogUtils.log("Datagen " + (isDatagen ? "enabled" : "disabled") + "!");
		return isDatagen;
	}

	/**
	 * This is set to true when Bootstrap.bootStrap() is finished.
	 */
	public static boolean isInitialized;

	// FROZENBLOCK MODS
	public static final String WILDER_WILD_MOD_ID = "wilderwild";
	public static final boolean HAS_WILDER_WILD = ModLoader.isModLoaded(WILDER_WILD_MOD_ID);

	public static final String TRAILIER_TALES_MOD_ID = "trailertales";
	public static final boolean HAS_TRAILIER_TALES = ModLoader.isModLoaded(TRAILIER_TALES_MOD_ID);

	public static final String THE_COPPERIER_AGE_MOD_ID = "thecopperierage";
	public static final boolean HAS_THE_COPPERIER_AGE = ModLoader.isModLoaded(THE_COPPERIER_AGE_MOD_ID);

	public static final String CHAOS_HYPERCUBED_MOD_ID = "chaoshypercubed";
	public static final boolean HAS_CHAOS_HYPERCUBED = ModLoader.isModLoaded(CHAOS_HYPERCUBED_MOD_ID);

	public static final String NETHERIER_NETHER_MOD_ID = "netheriernether";
	public static final boolean HAS_NETHERIER_NETHER = ModLoader.isModLoaded(NETHERIER_NETHER_MOD_ID);

	public static final String GLOWTONE_MOD_ID = "glowtone";
	public static final boolean HAS_GLOWTONE = ModLoader.isModLoaded(GLOWTONE_MOD_ID);

	public static final String SPRINGIER_LIFE_MOD_ID = "springierlife";
	public static final boolean HAS_SPRINGIER_LIFE = ModLoader.isModLoaded(SPRINGIER_LIFE_MOD_ID);

	public static final String SIMPLE_COPPER_PIPES_MOD_ID = "simple_copper_pipes";
	public static final boolean HAS_SIMPLE_COPPER_PIPES = ModLoader.isModLoaded(SIMPLE_COPPER_PIPES_MOD_ID);

	// EXTERNAL MODS
	public static final boolean HAS_MODMENU = ModLoader.isModLoaded("modmenu");
	// Cloth has a separate id on Fabric vs. Neo
	public static final boolean HAS_CLOTH_CONFIG = ModLoader.isModLoaded("cloth-config", "cloth_config");
	public static final boolean HAS_SODIUM = ModLoader.isModLoaded("sodium");
	public static final boolean HAS_TERRABLENDER = ModLoader.isModLoaded("terrablender");
}
