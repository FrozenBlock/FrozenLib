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

package net.frozenblock.lib.texture.client.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerTextureDownloader {
	public static final Map<String, Identifier> WAITING_TEXTURES = new HashMap<>();
	private static final List<Identifier> LOADED_TEXTURES = new ArrayList<>();
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String LOCAL_TEXTURE_SOURCE = ".local";

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

	@Nullable
	public static NativeImage downloadServerTexture(@Nullable Identifier texture, String destPath, String fileName) throws IOException {
		final Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(destPath);
		final Path possibleLocalPath = path.resolve(LOCAL_TEXTURE_SOURCE).resolve(fileName);

		if (Files.isRegularFile(possibleLocalPath)) {
			LOGGER.debug("Loading server texture from local cache ({})", destPath);
			final InputStream inputStream = Files.newInputStream(possibleLocalPath);

			NativeImage image;
			try {
				image = NativeImage.read(inputStream);
			} catch (Throwable cannotRead) {
				try {
					inputStream.close();
				} catch (Throwable cannotClose) {
					cannotRead.addSuppressed(cannotClose);
				}
				throw cannotRead;
			}

			inputStream.close();

			if (texture != null) LOADED_TEXTURES.add(texture);
			WAITING_TEXTURES.remove(makePathFromRootAndDest(destPath, fileName));

			return image;
		} else {
			if (FrozenLibConfig.FILE_TRANSFER_CLIENT && texture != null) {
				ClientPlayNetworking.send(FileTransferPacket.createRequest(destPath, fileName));
				WAITING_TEXTURES.put(makePathFromRootAndDest(destPath, fileName), texture);
				LOGGER.debug("Requesting server texture from {}", path);
			}
			return null;
		}
	}

	@Contract(pure = true)
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
