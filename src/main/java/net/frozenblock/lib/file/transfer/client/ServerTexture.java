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

package net.frozenblock.lib.file.transfer.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import net.minecraft.Util;

/**
 * A texture that will use a .png file sent from the server.
 *
 * <p> Sends a file transfer request to the server if the needed texture file is not present on the client, unless the file transfer config is disabled.
 */
@Environment(EnvType.CLIENT)
public class ServerTexture extends SimpleTexture implements Tickable {
	public static final Map<String, ServerTexture> WAITING_TEXTURES = new HashMap<>();
	public static final String LOCAL_TEXTURE_SOURCE = ".local";
	private static final Logger LOGGER = LogUtils.getLogger();
	private final File file;
	private final String destPath;
	private final String fileName;
	@Nullable
	private final Runnable onDownloaded;
	boolean hasLocalSource;
	@Nullable
	private CompletableFuture<?> future;
	private boolean uploaded;
	private long timeSinceLastReference;
	private boolean isClosed;

	public ServerTexture(String destPath, String fileName, ResourceLocation fallback, @Nullable Runnable callback) {
		super(fallback);
		Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(destPath);
		File possibleLocalFile = path.resolve(LOCAL_TEXTURE_SOURCE).resolve(fileName).toFile();
		this.hasLocalSource = possibleLocalFile.exists();
		this.file = this.hasLocalSource ? possibleLocalFile : path.resolve(fileName).toFile();
		this.destPath = destPath;
		this.fileName = fileName;
		this.onDownloaded = callback;
		this.timeSinceLastReference = System.currentTimeMillis();
		if (!this.hasLocalSource) {
			WAITING_TEXTURES.put(this.destPath + "/" + this.fileName, this);
		}
	}

	private void loadCallback(NativeImage image) {
		if (this.onDownloaded != null) {
			this.onDownloaded.run();
		}

		Minecraft.getInstance().execute(() -> {
			this.uploaded = true;
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(() -> this.upload(image));
			} else {
				this.upload(image);
			}
		});
	}

	private void upload(@NotNull NativeImage image) {
		TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, true);
	}

	public void runFutureForTexture() {
		if (this.future == null) {
			this.future = CompletableFuture.runAsync(() -> {
				LOGGER.debug("Downloading server texture {}", this.fileName);
				try {
					InputStream inputStream = new FileInputStream(this.file);
					Minecraft.getInstance().execute(() -> {
						NativeImage nativeImagex = this.load(inputStream);
						if (nativeImagex != null) {
							this.loadCallback(nativeImagex);
						}
					});
				} catch (Exception e) {
					LOGGER.error("Couldn't download server texture", e);
				}
			}, Util.backgroundExecutor());
		}
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		Minecraft.getInstance().execute(() -> {
			if (!this.uploaded) {
				try {
					super.load(manager);
				} catch (IOException var3x) {
					LOGGER.warn("Failed to load server texture: {}", this.location, var3x);
				}

				this.uploaded = true;
			}
		});

		NativeImage nativeImage;
		if (this.file != null && this.file.exists() && this.file.isFile()) {
			LOGGER.debug("Loading server texture from local cache ({})", this.file);
			FileInputStream fileInputStream = new FileInputStream(this.file);
			nativeImage = this.load(fileInputStream);
			fileInputStream.close();
		} else {
			if (FrozenLibConfig.FILE_TRANSFER_CLIENT) {
				Minecraft.getInstance().getConnection().send(
					new ServerboundCustomPayloadPacket(FileTransferPacket.createRequest(this.destPath, this.fileName))
				);
			}
			nativeImage = null;
		}

		if (nativeImage != null) {
			this.loadCallback(nativeImage);
		}
	}

	@Nullable
	private NativeImage load(InputStream stream) {
		NativeImage nativeImage = null;

		try {
			nativeImage = NativeImage.read(stream);
		} catch (Exception e) {
			LOGGER.warn("Error while loading server texture", e);
		}

		return nativeImage;
	}

	public void updateReferenceTime() {
		this.timeSinceLastReference = System.currentTimeMillis();
		if (this.isClosed) {
			this.isClosed = false;
			try {
				this.load(Minecraft.getInstance().getResourceManager());
			} catch (Exception ignored) {
			}
		}
	}

	@Override
	public void tick() {
		if (!this.isClosed && System.currentTimeMillis() - this.timeSinceLastReference > 5000) {
			this.getTextureImage(Minecraft.getInstance().getResourceManager()).close();
			this.isClosed = true;
		}
	}
}
