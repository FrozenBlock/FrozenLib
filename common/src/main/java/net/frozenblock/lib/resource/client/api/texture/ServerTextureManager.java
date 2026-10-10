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

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.ModLoader;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureResources;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

@ClientOnly
public final class ServerTextureManager implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<Identifier, ServerTextureStatus> textures = new Object2ObjectOpenHashMap<>();
	private final TextureManager textureManager;
	private final ServerTextureDownloader downloader;

	public ServerTextureManager(TextureManager textureManager, Minecraft minecraft) {
		this.textureManager = textureManager;
		this.downloader = new ServerTextureDownloader(
			minecraft,
			minecraft.gameDirectory.toPath(),
			this::setPending,
			this::setFailure,
			this::setSuccess
		);
	}

	public Identifier getOrRequestTexture(
		Identifier location,
		String destinationPath,
		String fileName,
		Identifier fallback
	) {
		synchronized (this.textures) {
			final ServerTextureStatus status = this.textures.get(location);
			if (status instanceof ServerTextureStatus.Success success) {
				success.updateReferenceTimestamp();
				return location;
			}
			if (status != null) return fallback;

			return this.downloader.fetchOrRequestDownloadIfNotPresent(location, destinationPath, fileName, fallback);
		}
	}

	public void tick() {
		synchronized (this.textures) {
			final long currentTimeMillis = System.currentTimeMillis();
			this.textures.values().removeIf(texture -> {
				if (texture instanceof ServerTextureStatus.Timed timed && timed.expired(currentTimeMillis)) {
					timed.onExpiry(this.textureManager);
					return true;
				}
				return false;
			});


			this.textures.values().forEach(texture -> {
				if (texture instanceof ServerTextureStatus.Timed timed) timed.tick(currentTimeMillis);
			});
		}
	}

	public void resetData() {
		this.textures.values().forEach(texture -> {
			if (texture instanceof ServerTextureStatus.Timed timed) timed.onExpiry(this.textureManager);
		});
		this.textures.clear();
	}

	public synchronized void setPending(Identifier location, String destinationPath, String fileName) {
		synchronized (this.textures) {
			final ServerTextureStatus status = this.textures.put(location, new ServerTextureStatus.Pending(location, createPendingPathToFile(destinationPath, fileName)));
			if (status != null &&  !(status instanceof ServerTextureStatus.Pending) && ModLoader.isDevelopmentEnvironment()) {
				throw new IllegalStateException("Attempting to mark texture with non-pending status as pending!");
			}
			if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} status pending", location);
		}

	}

	public synchronized void setFailure(Identifier location) {
		synchronized (this.textures) {
			final ServerTextureStatus status = this.textures.put(location, new ServerTextureStatus.Failure());
			if (status instanceof ServerTextureStatus.Success && ModLoader.isDevelopmentEnvironment()) {
				throw new IllegalStateException("Attempting to mark texture with success status as failure!");
			}
			if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} status failed", location);
		}
	}

	public synchronized void setSuccess(Identifier location, NativeImage contents) {
		synchronized (this.textures) {
			this.textureManager.register(location, TextureResources.from2dImage(() -> "Server Texture " + location, contents));
			this.textures.put(location, new ServerTextureStatus.Success(location));
			if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} status succeeded", location);
		}
	}

	public synchronized void onFileDownloaded(String destinationPath, String fileName) {
		synchronized (this.textures) {
			final String pathToFile = ServerTextureManager.createPendingPathToFile(destinationPath, fileName);
			for (ServerTextureStatus status : this.textures.values()) {
				if (status instanceof ServerTextureStatus.Pending(Identifier location, String toFile) && toFile.equals(pathToFile)) {
					if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} has been downloaded", location);
					this.downloader.onFileDownloaded(location, destinationPath, fileName);
				}
			}
		}
	}

	public synchronized void onFileDownloadFailed(String destinationPath, String fileName) {
		synchronized (this.textures) {
			final String pathToFile = ServerTextureManager.createPendingPathToFile(destinationPath, fileName);
			for (ServerTextureStatus status : this.textures.values()) {
				if (status instanceof ServerTextureStatus.Pending(Identifier location, String toFile) && toFile.equals(pathToFile)) {
					if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} has failed to download", location);
					this.setFailure(location);
				}
			}
		}
	}

	public static String createPendingPathToFile(String path, String fileName) {
		return path + "/" + fileName;
	}

	@Override
	public void close() {
		this.resetData();
	}

	private sealed interface ServerTextureStatus permits ServerTextureStatus.Failure, ServerTextureStatus.Pending, ServerTextureStatus.Success {
		record Pending(Identifier location, String pathToFile) implements ServerTextureStatus {}

		record Failure(long failedTimestamp) implements ServerTextureStatus, Timed {
			private static final long LIFETIME = 5000L;

			public Failure() {
				this(System.currentTimeMillis());
			}

			@Override
			public boolean expired(long currentTimeMillis) {
				return currentTimeMillis - this.failedTimestamp > LIFETIME;
			}
		}

		final class Success implements ServerTextureStatus, Timed {
			private static final long DEFAULT_LIFETIME = 5000L;
			private final Identifier location;
			private final long lifetime;
			private long lastReferenceTimestamp;

			public Success(Identifier location, long lifetime) {
				this.location = location;
				this.lifetime = lifetime;
				this.updateReferenceTimestamp();
			}

			public Success(Identifier location) {
				this(location, DEFAULT_LIFETIME);
			}

			@Override
			public boolean expired(long currentTimeMillis) {
				return currentTimeMillis - this.lastReferenceTimestamp > this.lifetime;
			}

			@Override
			public void onExpiry(TextureManager textureManager) {
				textureManager.release(this.location);
				if (FrozenLibConstants.DEBUG_SERVER_TEXTURE) LOGGER.info("Server texture {} has been released", this.location);
			}

			public void updateReferenceTimestamp() {
				this.lastReferenceTimestamp = System.currentTimeMillis();
			}

			public Identifier location() {
				return this.location;
			}
		}

		sealed interface Timed permits Failure, Success {
			default void tick(long currentTimeMillis) {}

			boolean expired(long currentTimeMillis);

			default boolean expired() {
				return this.expired(System.currentTimeMillis());
			}

			default void onExpiry(TextureManager textureManager) {}
		}
	}
}
