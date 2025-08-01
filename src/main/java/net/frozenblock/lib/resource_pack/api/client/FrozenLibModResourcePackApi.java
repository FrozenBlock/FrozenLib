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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.ZipFile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.resource_pack.impl.client.PackDownloadToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FrozenLibModResourcePackApi {
	@ApiStatus.Internal
	public static final Path RESOURCE_PACK_DIRECTORY = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("resourcepacks");
	@ApiStatus.Internal
	public static final Path MOD_RESOURCE_PACK_DIRECTORY = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("mod_resourcepacks");
	@ApiStatus.Internal
	public static final Path MOD_RESOURCE_PACK_PENDING_EXTRACTION_DIRECTORY = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("mod_resourcepacks_pending_extraction");
	@ApiStatus.Internal
	public static final Path DOWNLOADED_RESOURCE_PACK_DIRECTORY = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("downloaded_resourcepacks");
	private static final Path HASH_FILE = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("mod_resource_pack_hashes.txt");
	private static final Path DOWNLOADED_PACK_LOG_FILE = FrozenLibConstants.FROZENLIB_GAME_DIRECTORY.resolve("mod_downloaded_resource_packs.txt");
	private static final List<String> HIDDEN_PACK_IDS = new ArrayList<>();
	private static final List<String> MOD_RESOURCE_PACK_IDS = new ArrayList<>();
	private static final List<ToastInfo> QUEUED_TOASTS = new ArrayList<>();
	private static final boolean SHOW_DEBUG_TOASTS = FrozenLibConstants.UNSTABLE_LOGGING;

	/**
	 * Finds .zip files within the mod's jar file inside the "frozenlib_resourcepacks" path, then copies them to FrozenLib's Resource Pack directory.
	 * <p>
	 * These Resource Packs will be force-enabled.
	 * @param container The {@link ModContainer} of the mod.
	 * @param packName The name of the zip file, without the ".zip" extension.
	 * @param isDoubleZip Whether the Resource Pack is double-zipped.
	 * @param hidePackFromMenu Whether the Resource Pack should be hidden from the Resource Pack selection menu.
	 * @param skipHashCheck Whether the Resource Pack will still be extracted even if an identical version was already extracted prior.
	 * @throws IOException
	 */
	public static void findAndPrepareResourcePack(
		@NotNull ModContainer container, String packName, boolean isDoubleZip, boolean hidePackFromMenu, boolean skipHashCheck
	) throws IOException {
		String zipPackName = packName + ".zip";
		String packId = "frozenlib:mod/file/" + zipPackName;
		String subPath = "frozenlib_resourcepacks/" + zipPackName;

		// Mark the pack as registered by a mod
		MOD_RESOURCE_PACK_IDS.add(packId);
		// Mark the pack as hidden if needed
		if (hidePackFromMenu) registerHiddenPackId(packId);

		Optional<Path> resourcePack = container.findPath(subPath);
		if (resourcePack.isPresent()) {
			Path jarPackPath = resourcePack.get();

			// Calculate SHA256 hash of the jar's zip file
			String currentHash = calculateSHA256(jarPackPath);
			// Check if the hash has changed
			boolean hasHashChanged = skipHashCheck || hasHashChanged(packName, currentHash);

			// Hash has changed or this is a new pack, proceed with extraction
			File destFile = new File(MOD_RESOURCE_PACK_DIRECTORY.toString(), zipPackName);
			if (hasHashChanged || !destFile.exists()) {
				if (destFile.exists()) {
					if (!hasHashChanged) return;
					destFile.delete();
				}

				InputStream inputFromJar = Files.newInputStream(jarPackPath);
				if (isDoubleZip) {
					File extractionFile = new File(MOD_RESOURCE_PACK_PENDING_EXTRACTION_DIRECTORY.toString(), zipPackName);
					FileUtils.copyInputStreamToFile(inputFromJar, extractionFile);
					inputFromJar.close();

					AtomicBoolean extracted = new AtomicBoolean(false);
					ZipFile zipFile = new ZipFile(extractionFile);
					zipFile.entries().asIterator().forEachRemaining(entry -> {
						if (entry.getName().equals(zipPackName) && !extracted.get()) {
							try {
								InputStream zipInputStream = zipFile.getInputStream(entry);
								FileUtils.copyInputStreamToFile(zipInputStream, destFile);
								zipInputStream.close();
								extracted.set(true);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					});

					extractionFile.delete();
					if (!extracted.get()) FrozenLibLogUtils.logWarning("Could not find internal Resource Pack of name " + zipPackName + "!");
				} else {
					FileUtils.copyInputStreamToFile(inputFromJar, destFile);
					inputFromJar.close();
				}
			}

			// Update the hash record after successful extraction
			updateHashRecord(packName, currentHash);
		} else {
			FrozenLibLogUtils.logWarning("Could not find internal Resource Pack of name " + zipPackName + "!");
		}
	}

	/**
	 * Downloads Resource Packs, using URLs contained within a list of {@link PackDownloadInfo}s.
	 * <p>
	 * These Resource Packs will be force-enabled, but may require resources to be reloaded depending on when they finish downloading.
	 * <p>
	 * Downloads will not occur if {@link FrozenLibConfig#packDownloading} is set to {@link PackDownloadSetting#DISABLED}.
	 * @param downloadGroup The Resource Packs to download, in {@link PackDownloadGroup} form.
	 * @param hidePackdFromMenu Whether the Resource Packs should be hidden from the Resource Pack selection menu.
	 * @param skipVersionCheck Whether the Resource Packs will still be downloaded even if an identical version was already downloaded prior.
	 */
	public static void downloadResourcePacks(@NotNull PackDownloadGroup downloadGroup, boolean hidePackdFromMenu, boolean skipVersionCheck) {
		downloadGroup.resetPackStatuses();
		downloadGroup.packs().forEach(downloadInfo -> downloadResourcePack(downloadInfo, hidePackdFromMenu, skipVersionCheck));
	}

	/**
	 * Downloads a Resource Pack, using a URL contained in an online {@link JsonElement}.
	 * <p>
	 * The Resource Packs will be force-enabled, but may require resources to be reloaded depending on when it finishes downloading.
	 * <p>
	 * Downloads will not occur if {@link FrozenLibConfig#packDownloading} is set to {@link PackDownloadSetting#DISABLED}.
	 * @param downloadInfo The {@link PackDownloadInfo} to pull the URL and pack name from.
	 * @param hidePackFromMenu Whether the Resource Pack should be hidden from the Resource Pack selection menu.
	 * @param skipVersionCheck Whether the Resource Pack will still be downloaded even if an identical version was already downloaded prior.
	 */
	public static void downloadResourcePack(@NotNull PackDownloadInfo downloadInfo, boolean hidePackFromMenu, boolean skipVersionCheck) {
		String packName = downloadInfo.getPackName();
		String zipPackName = packName + ".zip";
		String packId = "frozenlib:mod/downloaded/file/" + zipPackName;

		// Mark the pack as registered by a mod
		MOD_RESOURCE_PACK_IDS.add(packId);
		// Mark the pack as hidden if needed
		if (hidePackFromMenu) registerHiddenPackId(packId);

		if (FrozenLibConfig.get().packDownloading == PackDownloadSetting.DISABLED) return;

		CompletableFuture.supplyAsync(
			() -> {
				Optional<ToastInfo> failToast = Optional.empty();
				try {
					File destFile = new File(DOWNLOADED_RESOURCE_PACK_DIRECTORY.toString(), zipPackName);
					// Check if the pack already exists
					boolean isPackPresent = destFile.exists();
					// Select proper failure toast accordingly
					failToast = isPackPresent
						? SHOW_DEBUG_TOASTS ? Optional.of(new ToastInfo(downloadInfo, ToastType.FAILURE_PRESENT)) : Optional.empty()
						: Optional.of(new ToastInfo(downloadInfo, ToastType.FAILURE));

					// Connect online to attempt pack download
					URL url = URI.create(downloadInfo.getURL()).toURL();
					URLConnection request = url.openConnection();
					request.connect();

					// Parse JSON
					JsonElement parsedJson = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
					JsonObject packDir = parsedJson.getAsJsonObject();
					String packURL = packDir.get("pack").getAsString();
					int packVersion = packDir.get("version").getAsInt();

					// Check if the version has changed
					boolean hasDownloadVersionChanged = skipVersionCheck || hasDownloadVersionChanged(packName, packVersion);

					// Cancel download if pack is unchanged and already present
					if (!hasDownloadVersionChanged && isPackPresent) return SHOW_DEBUG_TOASTS
						? Optional.of(new ToastInfo(downloadInfo, ToastType.PRESENT))
						: Optional.empty();

					Optional<File> downloadedPack = downloadPackFromURL(packURL, packName, destFile, packVersion);
					return downloadedPack.isPresent()
						? Optional.of(new ToastInfo(downloadInfo, isPackPresent ? ToastType.SUCCESS_UPDATE : ToastType.SUCCESS_DOWNLOAD))
						: failToast;
				} catch (IOException ignored) {
					return failToast;
				}
			},
			Executors.newCachedThreadPool()
		).whenComplete((value, throwable) -> {
			value.ifPresent(failToast -> ((ToastInfo)failToast).addToast());
		});
	}

	/**
	 * Downloads a Resource Pack from a URL, and updates the download record to store the new pack version.
	 * @param urlString The URL, in {@link String} format, to download the .zip from.
	 * @param packName The name of the Resource Pack.
	 * @param destFile The destination {@link File} of the Resource Pack.
	 * @param newVersion The new version number of the Resource Pack to store to the download record.
	 */
	private static Optional<File> downloadPackFromURL(String urlString, String packName, File destFile, int newVersion) {
		try {
			if (destFile.exists()) destFile.delete();

			URL url = URI.create(urlString).toURL();
			URLConnection request = url.openConnection();
			request.connect();

			InputStream input = (InputStream) request.getContent();
			FileUtils.copyInputStreamToFile(input, destFile);
			input.close();

			// Update the download record after successful download
			updateDownloadRecord(packName, newVersion);

			return Optional.of(destFile);
		} catch (IOException ignored) {
			FrozenLibConstants.LOGGER.error("Failed to download pack from URL: {}", urlString);
		}
		return Optional.empty();
	}

	/**
	 * Lets you specify the id of a Resource Pack to hide from the selection menu.
	 */
	public static void registerHiddenPackId(String packId) {
		HIDDEN_PACK_IDS.add(packId);
	}

	/**
	 * @return Whether the Resource Pack with the specified id should be hidden from the Resource Pack selection menu.
	 */
	public static boolean isPackHiddenFromMenu(String packId) {
		return HIDDEN_PACK_IDS.contains(packId);
	}

	/**
	 * @return Whether the Resource Pack with the specified id is currently registered by a mod.
	 */
	public static boolean isFrozenLibPackRegisteredByMod(String packId) {
		return MOD_RESOURCE_PACK_IDS.contains(packId);
	}

	/**
	 * Calculates the SHA256 hash of a {@link Path}.
	 * @param path The {@link Path} to calculate the hash for.
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
				String packName = entry.getKey();
				if (!new File(MOD_RESOURCE_PACK_DIRECTORY.toString(), packName + ".zip").exists()) continue;
				content.append(packName).append("=").append(entry.getValue()).append("\n");
			}
			Files.write(HASH_FILE, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// If we can't write the hash file, we'll continue without it
		}
	}

	/**
	 * Checks if the hash of a Resource Pack has changed.
	 * @param packName The name of the Resource Pack.
	 * @param currentHash The current SHA256 hash of the Resource Pack.
	 * @return true if the hash has changed or if this is a new pack, false otherwise.
	 */
	private static boolean hasHashChanged(String packName, String currentHash) {
		Map<String, String> existingHashes = readHashRecords();
		String existingHash = existingHashes.get(packName);
		return existingHash == null || !existingHash.equals(currentHash);
	}

	/**
	 * Updates the hash record for a Resource Pack.
	 * @param packName The name of the Resource Pack.
	 * @param newHash The new SHA256 hash of the Resource Pack.
	 */
	private static void updateHashRecord(String packName, String newHash) {
		Map<String, String> hashes = readHashRecords();
		hashes.put(packName, newHash);
		writeHashRecords(hashes);
	}

	/**
	 * Reads the download records from the downloads file.
	 * @return A map of pack names to their versions.
	 */
	private static Map<String, Integer> readDownloadRecords() {
		Map<String, Integer> hashes = new HashMap<>();
		if (Files.exists(DOWNLOADED_PACK_LOG_FILE)) {
			try {
				Files.lines(DOWNLOADED_PACK_LOG_FILE).forEach(line -> {
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						hashes.put(parts[0], Integer.valueOf(parts[1]));
					}
				});
			} catch (IOException e) {
				// If we can't read the hash file, we'll treat it as empty
			}
		}
		return hashes;
	}

	/**
	 * Writes the download records to the downloads file.
	 * @param versions A map of pack names to their versions.
	 */
	private static void writeDownloadRecords(Map<String, Integer> versions) {
		try {
			Files.createDirectories(FrozenLibConstants.FROZENLIB_GAME_DIRECTORY);
			StringBuilder content = new StringBuilder();
			for (Map.Entry<String, Integer> entry : versions.entrySet()) {
				String packName = entry.getKey();
				if (!new File(DOWNLOADED_RESOURCE_PACK_DIRECTORY.toString(), packName + ".zip").exists()) continue;
				content.append(packName).append("=").append(entry.getValue()).append("\n");
			}
			Files.write(DOWNLOADED_PACK_LOG_FILE, content.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// If we can't write the downloads file, we'll continue without it
		}
	}

	/**
	 * Checks if the version of a Resource Pack has changed.
	 * @param packName The name of the Resource Pack.
	 * @param currentVersion The current version of the Resource Pack.
	 * @return true if the version has changed or if this is a new pack, false otherwise.
	 */
	private static boolean hasDownloadVersionChanged(String packName, int currentVersion) {
		Map<String, Integer> existingDownloads = readDownloadRecords();
		Integer existingVersion = existingDownloads.get(packName);
		return existingVersion == null || !existingVersion.equals(currentVersion);
	}

	/**
	 * Updates the download record for a Resource Pack.
	 * @param packName The name of the Resource Pack.
	 * @param newVersion The new version of the Resource Pack.
	 */
	private static void updateDownloadRecord(String packName, int newVersion) {
		Map<String, Integer> downloads = readDownloadRecords();
		downloads.put(packName, newVersion);
		writeDownloadRecords(downloads);
	}

	@ApiStatus.Internal
	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> QUEUED_TOASTS.removeIf(ToastInfo::addToast));
	}

	@ApiStatus.Internal
	private record ToastInfo(PackDownloadInfo downloadInfo, ToastType toastType) {
		public boolean addToast() {
			if (FrozenLibConfig.get().packDownloading != PackDownloadSetting.ENABLED) return false;
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft == null || minecraft.getToasts() == null || minecraft.getResourceManager() == null) {
				if (!QUEUED_TOASTS.contains(this)) QUEUED_TOASTS.add(this);
				return false;
			}
			this.toastType.displayToast(this.downloadInfo);
			return true;
		}
	}

	@ApiStatus.Internal
	private enum ToastType {
		SUCCESS_DOWNLOAD(downloadInfo -> displayOrUpdateToast(PackDownloadToast.PackDownloadToastId.PACK_DOWNLOAD_SUCCESS, downloadInfo)),
		SUCCESS_UPDATE(downloadInfo -> displayOrUpdateToast(PackDownloadToast.PackDownloadToastId.PACK_UPDATE_SUCCESS, downloadInfo)),
		FAILURE(downloadInfo -> displayOrUpdateToast(PackDownloadToast.PackDownloadToastId.PACK_DOWNLOAD_FAILURE, downloadInfo)),
		FAILURE_PRESENT(downloadInfo -> displayOrUpdateToast(PackDownloadToast.PackDownloadToastId.PACK_DOWNLOAD_FAILURE_PRESENT, downloadInfo)),
		PRESENT(downloadInfo -> displayOrUpdateToast(PackDownloadToast.PackDownloadToastId.PACK_DOWNLOAD_PRESENT, downloadInfo));
		private final Consumer<PackDownloadInfo> toastMaker;

		ToastType(Consumer<PackDownloadInfo> toastMaker) {
			this.toastMaker = toastMaker;
		}

		public void displayToast(PackDownloadInfo downloadInfo) {
			this.toastMaker.accept(downloadInfo);
		}

		private static void displayOrUpdateToast(PackDownloadToast.PackDownloadToastId id, @NotNull PackDownloadInfo downloadInfo) {
			downloadInfo.setGroupStatus(id);
			PackDownloadToast.addOrAppendIfNotPresent(Minecraft.getInstance().getToasts(), id, downloadInfo);
		}
	}

	@ApiStatus.Internal
	public enum PackDownloadSetting implements StringRepresentable {
		ENABLED("pack_downloading.enabled"),
		ENABLED_NO_TOASTS("pack_downloading.enabled_no_toasts"),
		DISABLED("pack_downloading.disabled");
		private final String name;

		PackDownloadSetting(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public @NotNull String getSerializedName() {
			return this.name;
		}
	}

	public static class PackDownloadGroup implements PackDownloadStatusProvider {
		private final String groupName;
		private final List<PackDownloadInfo> packs = new ArrayList<>();
		private final Map<PackDownloadToast.PackDownloadToastId, List<PackDownloadInfo>> packStatuses = new Object2ObjectLinkedOpenHashMap<>();

		private PackDownloadGroup(String packGroup) {
			this.groupName = packGroup;
		}

		@Contract("_ -> new")
		public static @NotNull PackDownloadGroup create(String packGroup) {
			return new PackDownloadGroup(packGroup);
		}

		public PackDownloadGroup add(String url, String packName) {
			this.packs.add(PackDownloadInfo.of(url, packName, this));
			return this;
		}

		public void setPackStatus(PackDownloadToast.PackDownloadToastId id, PackDownloadInfo info) {
			for (List<PackDownloadInfo> list : this.packStatuses.values()) list.removeIf(foundInfo -> foundInfo.equals(info));
			this.packStatuses.computeIfAbsent(id, toastId -> new ArrayList<>()).add(info);
		}

		public void resetPackStatuses() {
			this.packStatuses.clear();
		}

		public int size() {
			return this.packs.size();
		}

		public int getPacksWithStatus(PackDownloadToast.PackDownloadToastId id) {
			return this.packStatuses.getOrDefault(id, List.of()).size();
		}

		public List<PackDownloadInfo> packs() {
			return this.packs;
		}

		public String getGroupName() {
			return this.groupName;
		}

		@Override
		public PackDownloadStatusProvider getDirectProvider() {
			return this;
		}

		@Override
		public Component getComponent(PackDownloadToast.PackDownloadToastId id) {
			return Component.translatable(
				"frozenlib.resourcepack.download.group",
				Component.translatable("frozenlib.resourcepack.group." + this.groupName),
				this.getPacksWithStatus(id),
				this.size()
			);
		}
	}

	public static class PackDownloadInfo implements PackDownloadStatusProvider {
		private final String url;
		private final String packName;
		private final Optional<PackDownloadGroup> packGroup;

		private PackDownloadInfo(String url, String packName, Optional<PackDownloadGroup> packGroup) {
			this.url = url;
			this.packName = packName;
			this.packGroup = packGroup;
		}

		@Contract("_, _, _ -> new")
		public static @NotNull PackDownloadInfo of(String url, String packName, PackDownloadGroup packGroup) {
			return new PackDownloadInfo(url, packName, Optional.ofNullable(packGroup));
		}

		@Contract("_, _ -> new")
		public static @NotNull PackDownloadInfo of(String url, String packName) {
			return new PackDownloadInfo(url, packName, Optional.empty());
		}

		public void setGroupStatus(PackDownloadToast.PackDownloadToastId id) {
			if (this.packGroup.isEmpty()) return;
			this.packGroup.get().setPackStatus(id, this);
		}

		public String getURL() {
			return this.url;
		}

		public String getPackName() {
			return this.packName;
		}

		public Optional<PackDownloadGroup> getPackGroup() {
			return this.packGroup;
		}

		@Override
		public PackDownloadStatusProvider getDirectProvider() {
			if (this.packGroup.isEmpty()) return this;
			return this.packGroup.get();
		}

		@Override
		public Component getComponent(PackDownloadToast.PackDownloadToastId id) {
			return Component.translatable("frozenlib.resourcepack.pack." + this.packName);
		}
	}

	public interface PackDownloadStatusProvider {
		PackDownloadStatusProvider getDirectProvider();
		Component getComponent(PackDownloadToast.PackDownloadToastId id);
	}
}
