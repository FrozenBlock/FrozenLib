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

package net.frozenblock.lib;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

@ApiStatus.Internal
public final class FrozenSharedConstants {
	public static final String PROJECT_ID = "FrozenLib";
	public static final String MOD_ID = "frozenlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(PROJECT_ID);
	public static final NOPLogger LOGGER4 = NOPLogger.NOP_LOGGER;
	/**
	 * Used for features that may be unstable and crash in public builds.
	 * <p>
	 * It's smart to use this for at least registries.
	 */
	public static boolean UNSTABLE_LOGGING = FabricLoader.getInstance().isDevelopmentEnvironment();
	public static final int DATA_VERSION = 2;

	@Contract("_ -> new")
	@NotNull
	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(FrozenSharedConstants.MOD_ID, path);
	}

	@NotNull
	public static String string(String path) {
		return id(path).toString();
	}

	private FrozenSharedConstants() {}
}
