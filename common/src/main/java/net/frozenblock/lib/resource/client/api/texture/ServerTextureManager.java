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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.frozenblock.lib.platform.ModLoader;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureResources;
import net.minecraft.resources.Identifier;

@ClientOnly
public final class ServerTextureManager implements AutoCloseable {
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
		final ServerTextureStatus status = this.textures.get(location);
		if (status instanceof ServerTextureStatus.Success success) {
			success.updateReferenceTimestamp();
			return location;
		}
		if (status != null) return fallback;

		return this.downloader.fetchOrRequestDownloadIfNotPresent(location, destinationPath, fileName, fallback);
	}

	public void tick() {
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

	public void resetData() {
		this.textures.values().forEach(texture -> {
			if (texture instanceof ServerTextureStatus.Timed timed) timed.onExpiry(this.textureManager);
		});
		this.textures.clear();
	}

	public void setPending(Identifier location, String destinationPath, String fileName) {
		final ServerTextureStatus status = this.textures.put(location, new ServerTextureStatus.Pending(location, createPendingPathToFile(destinationPath, fileName)));
		if (!(status instanceof ServerTextureStatus.Pending) && ModLoader.isDevelopmentEnvironment()) {
			throw new IllegalStateException("Attempting to mark texture with non-pending status as pending!");
		}
	}

	public void setFailure(Identifier location) {
		final ServerTextureStatus status = this.textures.put(location, new ServerTextureStatus.Failure());
		if (status instanceof ServerTextureStatus.Success && ModLoader.isDevelopmentEnvironment()) {
			throw new IllegalStateException("Attempting to mark texture with success status as failure!");
		}
	}

	public void setSuccess(Identifier location, NativeImage contents) {
		this.textures.put(location, new ServerTextureStatus.Success(location, TextureResources.from2dImage(() -> "Server Texture " + location, contents)));
	}

	public void onFileDownloaded(String destinationPath, String fileName) {
		final String pathToFile = ServerTextureManager.createPendingPathToFile(destinationPath, fileName);
		for (ServerTextureStatus status : this.textures.values()) {
			if (status instanceof ServerTextureStatus.Pending(Identifier location, String toFile) && toFile.equals(pathToFile)) {
				this.downloader.onFileDownloaded(location, destinationPath, fileName);
			}
		}
	}

	public void onFileDownloadFailed(String destinationPath, String fileName) {
		final String pathToFile = ServerTextureManager.createPendingPathToFile(destinationPath, fileName);
		for (ServerTextureStatus status : this.textures.values()) {
			if (status instanceof ServerTextureStatus.Pending(Identifier location, String toFile) && toFile.equals(pathToFile)) {
				this.setFailure(location);
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
			private final TextureResources resources;
			private final long lifetime;
			private long lastReferenceTimestamp;

			public Success(Identifier location, TextureResources resources, long lifetime) {
				this.location = location;
				this.resources = resources;
				this.lifetime = lifetime;
				this.updateReferenceTimestamp();
			}

			public Success(Identifier location, TextureResources resources) {
				this(location, resources, DEFAULT_LIFETIME);
			}

			@Override
			public void tick(long currentTimeMillis) {
				this.lastReferenceTimestamp = currentTimeMillis;
			}

			@Override
			public boolean expired(long currentTimeMillis) {
				return currentTimeMillis - this.lastReferenceTimestamp > this.lifetime;
			}

			@Override
			public void onExpiry(TextureManager textureManager) {
				textureManager.release(this.location);
			}

			public void updateReferenceTimestamp() {
				this.lastReferenceTimestamp = System.currentTimeMillis();
			}

			public Identifier location() {
				return this.location;
			}

			public TextureResources resources() {
				return this.resources;
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
