package net.frozenblock.lib.platform.service;

import java.nio.file.Path;

public interface LoaderHelper {
	boolean isDevelopmentEnvironment();
	Path getGameDir();
}
