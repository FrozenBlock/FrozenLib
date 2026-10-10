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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.file.transfer.FileTransferEvents;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@ClientOnly
public final class ServerTextureDownloader {
	private static final List<String> VALID_FILE_EXTENSIONS = ImmutableList.of("png", "jpeg", "mcphoto");
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Executor mainThreadExecutor;
	private final Path gameDirectory;
	private final TriConsumer<Identifier, String, String> onRequest;
	private final Consumer<Identifier> onFailure;
	private final BiConsumer<Identifier, NativeImage> onSuccess;

	public ServerTextureDownloader(
		Executor mainThreadExecutor,
		Path gameDirectory,
		TriConsumer<Identifier, String, String> onRequest,
		Consumer<Identifier> onFailure,
		BiConsumer<Identifier, NativeImage> onSuccess
	) {
		this.mainThreadExecutor = mainThreadExecutor;
		this.gameDirectory = gameDirectory;
		this.onRequest = onRequest;
		this.onFailure = onFailure;
		this.onSuccess = onSuccess;
	}

	public Identifier fetchOrRequestDownloadIfNotPresent(Identifier textureId, String destinationPath, String fileName, Identifier fallback) {
		this.onRequest.accept(textureId, destinationPath, fileName);

		return CompletableFuture.supplyAsync(
			() -> {
				NativeImage image;
				try {
					image = this.loadLocalTextureOrRequestDownload(textureId, destinationPath, fileName);
				} catch (IOException exception) {
					throw new UncheckedIOException(exception);
				}
				return image;
			},
			Util.nonCriticalIoPool().forName("loadOrDownloadServerTexture")
		).thenCompose(image -> this.onImageLoaded(textureId, image))
			.getNow(fallback);
	}

	public void onFileDownloaded(Identifier textureId, String destinationPath, String fileName) {
		CompletableFuture.supplyAsync(
			() -> {
				NativeImage image;
				try {
					final Path filePath = this.gameDirectory.resolve(destinationPath).resolve(fileName);
					final String fileExtension = FilenameUtils.getExtension(fileName);
					image = loadImageAsPNG(filePath, destinationPath, fileExtension);
				} catch (IOException exception) {
					throw new UncheckedIOException(exception);
				}
				return image;
			},
			Util.nonCriticalIoPool().forName("loadServerTextureUponDownload")
		).thenCompose(image -> this.onImageLoaded(textureId, image));
	}

	@Nullable
	private synchronized NativeImage loadLocalTextureOrRequestDownload(Identifier textureId, String destinationPath, String fileName) throws IOException {
		final Path directory = this.gameDirectory.resolve(destinationPath);
		for (String fileExtension : VALID_FILE_EXTENSIONS) {
			final String fileNameWithExtension = fileName + "." + fileExtension;
			final Path destination = directory.resolve(fileNameWithExtension);
			final Path localSource = directory.resolve(FileTransferPacket.LOCAL_SOURCE).resolve(fileNameWithExtension);
			final Path sourcePath = Files.isRegularFile(destination) ? destination : localSource;

			final NativeImage image = loadImageAsPNG(sourcePath, destinationPath, fileExtension);
			if (image != null) return image;
		}

		if (FrozenLibConfig.FILE_TRANSFER_CLIENT.get() && textureId != null) {
			ClientNetworkingHelper.sendToServer(FileTransferPacket.createRequest(destinationPath, fileName, VALID_FILE_EXTENSIONS));
			FileTransferEvents.REQUEST_SEND.invoker().onRequestSent(destinationPath, fileName, VALID_FILE_EXTENSIONS, true);

			if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) {
				LOGGER.info("Requesting server texture {}/{} from server", destinationPath, fileName);
			} else if (FrozenLibConstants.UNSTABLE_LOGGING) {
				LOGGER.debug("Requesting server texture {}/{} from server", destinationPath, fileName);
			}
		}

		return null;
	}

	@Nullable
	private static synchronized NativeImage loadImageAsPNG(Path imagePath, String path, String fileExtension) throws IOException {
		if (!Files.isRegularFile(imagePath)) return null;

		if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) {
			LOGGER.info("Loading server texture from local cache ({})", path);
		} else if (FrozenLibConstants.UNSTABLE_LOGGING) {
			LOGGER.debug("Loading server texture from local cache ({})", path);
		}

		final InputStream inputStream = Files.newInputStream(imagePath);
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

		return image;
	}

	private synchronized CompletableFuture<Identifier> onImageLoaded(Identifier textureId, @Nullable NativeImage contents) {
		if (contents == null) return CompletableFuture.completedFuture(null);

		return CompletableFuture.supplyAsync(() -> {
			try (contents) {
				this.onSuccess.accept(textureId, contents);
			} catch (Throwable cannotRead) {
				this.onFailure.accept(textureId);
			}

			return textureId;
		}, this.mainThreadExecutor);
	}
}
