package net.frozenblock.lib.core.entrypoint;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import java.util.function.Consumer;

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
