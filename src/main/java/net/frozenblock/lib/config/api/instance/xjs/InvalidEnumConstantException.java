/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.instance.xjs;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
public class InvalidEnumConstantException extends RuntimeException {
	public InvalidEnumConstantException(final String name, final Class<? extends Enum<?>> clazz) {
		super(createMessage(name, clazz));
	}

	private static String createMessage(final String name, final Class<? extends Enum<?>> clazz) {
		final String values = Arrays.toString(clazz.getEnumConstants());
		return f("{} \"{}\" does not exist. Valid options are: {}", clazz.getSimpleName(), name, values);
	}

	/**
	 * Interpolates strings by replacing instances of <code>{}</code> in order.
	 *
	 * @param s The template string being formatted.
	 * @param args A list of arguments to interpolate into this string.
	 * @return A formatted, interpolated string.
	 */
	@NotNull
	static String f(final String s, final Object... args) {
		int begin = 0, si = 0, oi = 0;
		final StringBuilder sb = new StringBuilder();
		while (true) {
			si = s.indexOf("{}", si);
			if (si >= 0) {
				sb.append(s, begin, si);
				sb.append(args[oi++]);
				begin = si = si + 2;
			} else {
				break;
			}
		}
		sb.append(s.substring(begin));
		return sb.toString();
	}
}
