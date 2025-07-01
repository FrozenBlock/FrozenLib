/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.resource_pack.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipFile;

@Environment(EnvType.CLIENT)
public class FrozenLibModResourcePackApi {
	@ApiStatus.Internal
	public static final Path RESOURCE_PACK_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("frozenlib").resolve("resourcepacks");

	/**
	 * Finds .zip files within the mod's jar file inside the "frozenlib_resourcepacks" path, then extracts them to the game's run directory.
	 * <p>
	 * Note that this has only been tested on double-zipped resource packs, as means of permitting the use of obfuscated resource packs within mods.
	 * <p>
	 * These resource packs will be force-enabled.
	 * @param container The {@link ModContainer} of the mod.
	 * @param packName The name of the zip file, without the ".zip" extension.
	 * @throws IOException
	 */
	public static void findAndExtractAllResourcePackZips(@NotNull ModContainer container, String packName) throws IOException {
		String subPath = "frozenlib_resourcepacks/" + packName + ".zip";

		Optional<Path> resourcePack = container.findPath(subPath);
		if (resourcePack.isPresent()) {
			Path path = resourcePack.get();
			ZipFile zip = new ZipFile(path.toFile());

			zip.entries().asIterator().forEachRemaining(entry -> {
				String name = entry.getName();
				File destFile = new File(RESOURCE_PACK_DIRECTORY.toString(), name);
				if (destFile.exists()) destFile.delete();

				try {
					InputStream zipInputStream = zip.getInputStream(entry);
					FileUtils.copyInputStreamToFile(zipInputStream, destFile);
					zipInputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});

			zip.close();
		}
	}

}
