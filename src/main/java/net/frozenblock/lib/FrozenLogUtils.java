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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class FrozenLogUtils {
	private FrozenLogUtils() {}

	public static void log(Object string, boolean should, Object... args) {
		if (should) {
			FrozenSharedConstants.LOGGER.info(string.toString());
		}
	}

	public static void log(Object string, Object... args) {
		log(string, true, args);
	}

	public static void logWarning(Object string, boolean should, Object... args) {
		if (should) {
			FrozenSharedConstants.LOGGER.warn(string.toString());
		}
	}

	public static void logWarning(Object string, Object... args) {
		logWarning(string, true, args);
	}

	public static void logError(Object string, boolean should, Object... args) {
		if (should) {
			FrozenSharedConstants.LOGGER.error(string.toString(), args);
		}
	}

	public static void logError(Object string, Object... args) {
		logError(string, true, args);
	}
}
