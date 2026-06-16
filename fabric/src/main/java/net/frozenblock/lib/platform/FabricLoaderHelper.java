package net.frozenblock.lib.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.platform.service.LoaderHelper;
import java.nio.file.Path;

public class FabricLoaderHelper implements LoaderHelper {
	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public Path getGameDir() {
		return FabricLoader.getInstance().getGameDir();
	}
}
