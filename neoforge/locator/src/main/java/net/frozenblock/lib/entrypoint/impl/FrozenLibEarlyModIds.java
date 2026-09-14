package net.frozenblock.lib.entrypoint.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IDependencyLocator;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;

public final class FrozenLibEarlyModIds implements IDependencyLocator {

	@Override
	public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
		final List<String> modIds = new ArrayList<>();
		for (IModFile modFile : loadedMods) {
			for (IModInfo modInfo : modFile.getModInfos()) {
				modIds.add(modInfo.getModId());
			}
		}
		setEarlyModIds(modIds);
	}

	@Override
	public int getPriority() {
		return LOWEST_SYSTEM_PRIORITY;
	}

	private static void setEarlyModIds(List<String> modIds) {
		try {
			final Class<?> modLoaderImpl = Class.forName("net.frozenblock.lib.platform.platform.ModLoaderImpl");
			final Field field = modLoaderImpl.getField("EARLY_MOD_IDS");
			field.set(null, List.copyOf(modIds));
		} catch (ReflectiveOperationException | LinkageError | SecurityException _) {}
	}
}
