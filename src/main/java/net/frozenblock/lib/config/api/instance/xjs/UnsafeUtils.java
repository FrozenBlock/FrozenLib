package net.frozenblock.lib.config.api.instance.xjs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

// Source: Cloth Config
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
