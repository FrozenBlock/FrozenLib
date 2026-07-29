package net.frozenblock.lib.entrypoint.impl;

import java.io.IOException;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.neoforged.neoforgespi.locating.IDependencyLocator;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFile;

public class FrozenLibLocator implements IDependencyLocator {

	@Override
	public void scanMods(List<IModFile> loadedMods, IDiscoveryPipeline pipeline) {
		for (IModFile modFile : loadedMods) {
			try {
				var stream = modFile.getContents().openFile(FrozenLibEntrypoints.METADATA_FILE);
				if (stream != null) FrozenLibEntrypoints.collectFromStream(modFile.getId(), stream);
			} catch (IOException e) {
				FrozenLibConstants.LOGGER.error("Failed to open {} for mod file {}", FrozenLibEntrypoints.METADATA_FILE, modFile.getId(), e);
			}
		}
		FrozenLibEntrypoints.markCollected();
	}
}
