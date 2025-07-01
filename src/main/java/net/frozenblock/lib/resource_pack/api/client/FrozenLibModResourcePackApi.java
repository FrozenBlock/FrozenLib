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
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.FrozenLibConstants;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipFile;

@Environment(EnvType.CLIENT)
public class FrozenLibModResourcePackApi {
	@ApiStatus.Internal
	public static final Path RESOURCE_PACK_DIRECTORY = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("resourcepacks");

	@ApiStatus.Internal
	public static final Path HASH_FILE = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("resource_pack_hashes.txt");

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

			// Calculate SHA256 hash of the extracted zip file
			String currentHash = calculateSHA256(path);
			// Check if the hash has changed
			boolean hasHashChanged = hasHashChanged(packName, currentHash);

			// Hash has changed or this is a new pack, proceed with extraction
			InputStream inputFromJar = Files.newInputStream(path);
			File extractionFile = new File(RESOURCE_PACK_DIRECTORY.resolve("pending_extraction").toFile(), packName + ".zip");
			FileUtils.copyInputStreamToFile(inputFromJar, extractionFile);
			inputFromJar.close();

			ZipFile zip = new ZipFile(extractionFile);

			zip.entries().asIterator().forEachRemaining(entry -> {
				String name = entry.getName();
				File destFile = new File(RESOURCE_PACK_DIRECTORY.toString(), name);
				if (destFile.exists()) {
					if (!hasHashChanged) return;
					destFile.delete();
				}

				try {
					InputStream zipInputStream = zip.getInputStream(entry);
					FileUtils.copyInputStreamToFile(zipInputStream, destFile);
					zipInputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});

			zip.close();

			// Clean up the temporary extraction file
			extractionFile.delete();

			// Update the hash record after successful extraction
			updateHashRecord(packName, currentHash);
		}
	}

	/**
	 * Calculates the SHA256 hash of a file.
	 * @param path The file to calculate the hash for.
	 * @return The SHA256 hash as a hexadecimal string.
	 * @throws IOException If an I/O error occurs.
	 */
	private static String calculateSHA256(Path path) throws IOException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] fileBytes = Files.readAllBytes(path);
			byte[] hashBytes = digest.digest(fileBytes);

			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not available", e);
		}
	}

	/**
	 * Reads the hash records from the hash file.
	 * @return A map of pack names to their SHA256 hashes.
	 */
	private static Map<String, String> readHashRecords() {
		Map<String, String> hashes = new HashMap<>();
		if (Files.exists(HASH_FILE)) {
			try {
				Files.lines(HASH_FILE).forEach(line -> {
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						hashes.put(parts[0], parts[1]);
					}
				});
			} catch (IOException e) {
				// If we can't read the hash file, we'll treat it as empty
			}
		}
		return hashes;
	}

	/**
	 * Writes the hash records to the hash file.
	 * @param hashes A map of pack names to their SHA256 hashes.
	 */
	private static void writeHashRecords(Map<String, String> hashes) {
		try {
			Files.createDirectories(FrozenLibConstants.FROZENLIB_GAME_DIRECTORY);
			StringBuilder content = new StringBuilder();
			for (Map.Entry<String, String> entry : hashes.entrySet()) {
				content.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
			}
			Files.write(HASH_FILE, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// If we can't write the hash file, we'll continue without it
		}
	}

	/**
	 * Checks if the hash of a resource pack has changed.
	 * @param packName The name of the resource pack.
	 * @param currentHash The current SHA256 hash of the resource pack.
	 * @return true if the hash has changed or if this is a new pack, false otherwise.
	 */
	private static boolean hasHashChanged(String packName, String currentHash) {
		Map<String, String> existingHashes = readHashRecords();
		String existingHash = existingHashes.get(packName);
		return existingHash == null || !existingHash.equals(currentHash);
	}

	/**
	 * Updates the hash record for a resource pack.
	 * @param packName The name of the resource pack.
	 * @param newHash The new SHA256 hash of the resource pack.
	 */
	private static void updateHashRecord(String packName, String newHash) {
		Map<String, String> hashes = readHashRecords();
		hashes.put(packName, newHash);
		writeHashRecords(hashes);
	}

}
