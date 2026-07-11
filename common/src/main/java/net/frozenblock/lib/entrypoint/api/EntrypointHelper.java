/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.entrypoint.api;

import java.util.function.Consumer;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;

public class EntrypointHelper {

	@PlatformImpl
	public static <T> void forEachEntrypoint(Class<T> clazz, Consumer<T> consumer) {
		throw new AssertionError();
	}

	public static void validateEntrypoint(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Entrypoint.class)) throw new IllegalArgumentException("Class " + clazz.getName() + " is not an entrypoint!");
	}

	public static Entrypoint getEntrypointInformation(Class<?> clazz) {
		validateEntrypoint(clazz);
		return clazz.getAnnotation(Entrypoint.class);
	}
}
