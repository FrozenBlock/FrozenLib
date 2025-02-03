/*
 * Copyright (C) 2024-2025 FrozenBlock
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
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

@Environment(EnvType.CLIENT)
public class ServerTextureDownloader {
	public static final Map<String, ResourceLocation> WAITING_TEXTURES = new HashMap<>();
	private static final List<ResourceLocation> LOADED_TEXTURES = new ArrayList<>();
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String LOCAL_TEXTURE_SOURCE = ".local";

	public static ResourceLocation getOrLoadServerTexture(
		ResourceLocation resourceLocation, String destPath, String string,
		ResourceLocation fallback
	) {
		downloadAndRegisterServerTexture(resourceLocation, destPath, string);
		if (LOADED_TEXTURES.contains(resourceLocation)) {
			return resourceLocation;
		} else {
			return fallback;
		}
	}

	public static void downloadAndRegisterServerTexture(
		ResourceLocation resourceLocation, String destPath, String fileName
	) {
		if (LOADED_TEXTURES.contains(resourceLocation)) return;
		CompletableFuture.supplyAsync(() -> {
			NativeImage nativeImage;
			try {
				nativeImage = downloadServerTexture(resourceLocation, destPath, fileName);
			} catch (IOException var5) {
				throw new UncheckedIOException(var5);
			}

			return nativeImage;
		}, Util.nonCriticalIoPool().forName("downloadServerTexture"))
			.thenCompose((nativeImage) -> registerTimedTextureInManager(resourceLocation, nativeImage, destPath, fileName));
	}

	public static @Nullable NativeImage downloadServerTexture(
		@Nullable ResourceLocation resourceLocation, String destPath, String fileName
	) throws IOException {
		Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(destPath);
		Path possibleLocalPath = path.resolve(LOCAL_TEXTURE_SOURCE).resolve(fileName);

		if (Files.isRegularFile(possibleLocalPath)) {
			LOGGER.debug("Loading server texture from local cache ({})", destPath);
			InputStream inputStream = Files.newInputStream(possibleLocalPath);

			NativeImage nativeImage;
			try {
				nativeImage = NativeImage.read(inputStream);
			} catch (Throwable throwable) {
				try {
					inputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}
				throw throwable;
			}

			inputStream.close();

			if (resourceLocation != null) LOADED_TEXTURES.add(resourceLocation);
			WAITING_TEXTURES.remove(makePathFromRootAndDest(destPath, fileName));

			return nativeImage;
		} else {
			if (FrozenLibConfig.FILE_TRANSFER_CLIENT && resourceLocation != null) {
				ClientPlayNetworking.send(FileTransferPacket.createRequest(destPath, fileName));
				WAITING_TEXTURES.put(makePathFromRootAndDest(destPath, fileName), resourceLocation);
				LOGGER.debug("Requesting server texture from {}", path);
			}
			return null;
		}
	}

	@Contract(pure = true)
	public static @NotNull String makePathFromRootAndDest(String path, String dest) {
		return path + "/" + dest;
	}

	private static @NotNull CompletableFuture<ResourceLocation> registerTimedTextureInManager(
		ResourceLocation resourceLocation,
		NativeImage nativeImage,
		String destPath,
		String fileName
	) {
		Minecraft minecraft = Minecraft.getInstance();
		return CompletableFuture.supplyAsync(() -> {
			minecraft.getTextureManager().register(resourceLocation, new ServerTexture(nativeImage, destPath, fileName));
			return resourceLocation;
		}, minecraft);
	}
}
