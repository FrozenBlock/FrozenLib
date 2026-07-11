package net.frozenblock.lib.core.entrypoint.platform;

import java.util.ServiceLoader;
import java.util.function.Consumer;
import net.frozenblock.lib.core.entrypoint.EntrypointHelper;

public class EntrypointHelperImpl {

	public static <T> void forEachEntrypoint(Class<T> clazz, Consumer<T> consumer) {
		EntrypointHelper.validateEntrypoint(clazz);
		ServiceLoader.load(clazz, EntrypointHelper.class.getClassLoader())
			.forEach(consumer);
	}
}
