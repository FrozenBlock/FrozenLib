/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.resource.client.api.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@ClientOnly
public class ServerTextureDownloader {
	private static final List<String> POSSIBLE_EXTENSIONS = ImmutableList.of("png", "jpeg", "mcphoto");
	public static final Map<String, Identifier> WAITING_TEXTURES = new HashMap<>();
	private static final List<Identifier> LOADED_TEXTURES = new ArrayList<>();
	private static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier getOrLoadServerTexture(
		Identifier texture, String destPath, String string,
		Identifier fallback
	) {
		downloadAndRegisterServerTexture(texture, destPath, string);
		if (LOADED_TEXTURES.contains(texture)) return texture;
		return fallback;
	}

	public static void downloadAndRegisterServerTexture(Identifier texture, String destPath, String fileName) {
		if (LOADED_TEXTURES.contains(texture)) return;

		CompletableFuture.supplyAsync(
			() -> {
				NativeImage image;
				try {
					image = downloadServerTexture(texture, destPath, fileName);
				} catch (IOException exception) {
					throw new UncheckedIOException(exception);
				}
				return image;
			},
			Util.nonCriticalIoPool().forName("downloadServerTexture")
		).thenCompose(image -> registerTimedTextureInManager(texture, image, destPath, fileName));
	}

	public static void registerTextureByPacketIfFound(String transferPath, String fileName) {
		final Identifier id = WAITING_TEXTURES.get(makePathFromRootAndDest(transferPath, fileName));
		if (id != null) {
			downloadAndRegisterServerTexture(id, transferPath, fileName);
			return;
		}

		final String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
		if (fileNameWithoutExtension.equals(fileName)) return;

		final Identifier idWithoutExtension = WAITING_TEXTURES.get(makePathFromRootAndDest(transferPath, fileNameWithoutExtension));
		if (idWithoutExtension != null) downloadAndRegisterServerTexture(idWithoutExtension, transferPath, fileNameWithoutExtension);
	}

	@Nullable
	public static NativeImage downloadServerTexture(@Nullable Identifier texture, String destPath, String fileName) throws IOException {
		final Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(destPath);
		for (String fileExtension : POSSIBLE_EXTENSIONS) {
			final String fixedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
			final String fileNameWithExtension = fileName + "." + fixedExtension;
			final Path destinationPath = path.resolve(fileNameWithExtension);
			final Path possibleLocalPath = path.resolve(FileTransferPacket.LOCAL_SOURCE).resolve(fileNameWithExtension);
			final Path readFromPath = Files.isRegularFile(destinationPath) ? destinationPath : possibleLocalPath;

			if (!Files.isRegularFile(readFromPath)) continue;

			LOGGER.debug("Loading server texture from local cache ({})", destPath);
			final InputStream inputStream = Files.newInputStream(readFromPath);
			InputStream imageInput = inputStream;

			try {
				if (fileExtension.equals("jpeg") || fileExtension.equals("mcphoto")) {
					final ByteArrayOutputStream output = new ByteArrayOutputStream();
					ImageIO.write(ImageIO.read(inputStream), "png", output);
					inputStream.close();

					imageInput = new ByteArrayInputStream(output.toByteArray());
					output.close();
				}
			} catch (Throwable cannotRead) {
				try {
					inputStream.close();
				} catch (Throwable cannotClose) {
					cannotRead.addSuppressed(cannotClose);
				}
				throw cannotRead;
			}

			NativeImage image;
			try {
				image = NativeImage.read(imageInput);
			} catch (Throwable cannotRead) {
				try {
					imageInput.close();
				} catch (Throwable cannotClose) {
					cannotRead.addSuppressed(cannotClose);
				}
				throw cannotRead;
			}

			imageInput.close();

			if (texture != null) LOADED_TEXTURES.add(texture);
			WAITING_TEXTURES.remove(makePathFromRootAndDest(destPath, fileName));

			return image;
		}

		if (FrozenLibConfig.FILE_TRANSFER_CLIENT.get() && texture != null) {
			ClientNetworkingHelper.sendToServer(FileTransferPacket.createRequest(destPath, fileName, POSSIBLE_EXTENSIONS));
			WAITING_TEXTURES.put(makePathFromRootAndDest(destPath, fileName), texture);
			if (FrozenLibConstants.UNSTABLE_LOGGING) LOGGER.debug("Requesting server texture {} from {}", fileName, path);
		}
		return null;
	}

	public static String makePathFromRootAndDest(String path, String dest) {
		return path + "/" + dest;
	}

	private static CompletableFuture<Identifier> registerTimedTextureInManager(Identifier texture, NativeImage image, String destPath, String fileName) {
		final Minecraft minecraft = Minecraft.getInstance();
		return CompletableFuture.supplyAsync(() -> {
			minecraft.getTextureManager().register(texture, new ServerTexture(image, destPath, fileName));
			return texture;
		}, minecraft);
	}
}
