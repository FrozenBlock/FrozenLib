/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class FrozenLogUtils {
	private FrozenLogUtils() {}

	public static void log(Object string, boolean should) {
		if (should) {
			FrozenSharedConstants.LOGGER.info(string.toString());
		}
	}

	public static void log(Object string) {
		log(string, true);
	}

	public static void logWarning(Object string, boolean should) {
		if (should) {
			FrozenSharedConstants.LOGGER.warn(string.toString());
		}
	}

	public static void logWarning(Object string) {
		logWarning(string, true);
	}

	public static void logError(Object string, boolean should, @Nullable Throwable throwable) {
		if (should) {
			FrozenSharedConstants.LOGGER.error(string.toString(), throwable);
		}
	}

	public static void logError(Object string, boolean should) {
		logError(string, should, null);
	}

	public static void logError(Object string, @Nullable Throwable throwable) {
		logError(string, true, throwable);
	}

	public static void logError(Object string) {
		logError(string, true, null);
	}
}
