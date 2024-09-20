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

package net.frozenblock.lib.config.api.instance.xjs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;

// Source: Cloth Config
@UtilityClass
public class UnsafeUtils {

	public static <V> V constructUnsafely(Class<V> cls) {
		try {
			Constructor<V> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (ReflectiveOperationException var2) {
			throw new RuntimeException(var2);
		}
	}

	@SuppressWarnings("unchecked")
	public static <V> V getUnsafely(Field field, Object obj) {
		if (obj == null) {
			return null;
		} else {
			try {
				field.setAccessible(true);
				return (V) field.get(obj);
			} catch (ReflectiveOperationException var3) {
				throw new RuntimeException(var3);
			}
		}
	}

	public static <V> V getUnsafely(Field field, Object obj, V defaultValue) {
		V ret = getUnsafely(field, obj);
		if (ret == null) {
			ret = defaultValue;
		}

		return ret;
	}

	public static void setUnsafely(Field field, Object obj, Object newValue) {
		if (obj != null) {
			try {
				field.setAccessible(true);
				field.set(obj, newValue);
			} catch (ReflectiveOperationException var4) {
				throw new RuntimeException(var4);
			}
		}
	}
}
