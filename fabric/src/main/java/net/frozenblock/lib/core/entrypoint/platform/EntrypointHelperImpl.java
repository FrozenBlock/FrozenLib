package net.frozenblock.lib.core.entrypoint.platform;

import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.core.entrypoint.EntrypointHelper;

public class EntrypointHelperImpl {

	public static <T> void forEachEntrypoint(Class<T> clazz, Consumer<T> consumer) {
		FabricLoader.getInstance()
			.getEntrypointContainers(EntrypointHelper.getEntrypointInformation(clazz).value(), clazz)
			.forEach(entrypoint -> consumer.accept(entrypoint.getEntrypoint()));
	}
}
