package net.frozenblock.lib.platform;

import net.frozenblock.lib.platform.service.LoaderHelper;
import net.neoforged.fml.loading.FMLLoader;
import java.nio.file.Path;

public class NeoLoaderHelper implements LoaderHelper {
	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	@Override
	public Path getGameDir() {
		return FMLLoader.getCurrent().getGameDir();
	}
}
