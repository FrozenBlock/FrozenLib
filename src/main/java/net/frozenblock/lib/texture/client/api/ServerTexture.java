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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TickableTexture;

/**
 * A texture that will use a .png file sent from the server.
 *
 * <p> Sends a file transfer request to the server if the needed texture file is not present on the client, unless the file transfer config is disabled.
 */
@Environment(EnvType.CLIENT)
public class ServerTexture extends DynamicTexture implements TickableTexture {
	private final String destPath;
	private final String fileName;
	private boolean closed;

	/**
	 * The amount of miliseconds after the texture's last usage to keep its {@link NativeImage} loaded before closing it.
	 */
	private final long timeInMilisBeforeClose;
	private long timeSinceLastReference;

	// TODO: Look into GpuTexture.getLabel(); usage
	public ServerTexture(NativeImage nativeImage, String destPath, String fileName) {
		super(() -> fileName, nativeImage);
		this.timeInMilisBeforeClose = 5000L;
		this.destPath = destPath;
		this.fileName = fileName;
	}

	public ServerTexture(NativeImage nativeImage, long timeInMilisBeforeClose, String destPath, String fileName) {
		super(() -> fileName, nativeImage);
		this.timeInMilisBeforeClose = timeInMilisBeforeClose;
		this.destPath = destPath;
		this.fileName = fileName;
	}

	public void updateReferenceTime() {
		this.timeSinceLastReference = System.currentTimeMillis();
		if (this.closed) {
			this.closed = false;
			try {
				this.setPixels(
					ServerTextureDownloader.downloadServerTexture(null, this.destPath, this.fileName)
				);
			} catch (Exception ignored) {}
		}
	}

	@Override
	public void tick() {
		if (!this.closed && System.currentTimeMillis() - this.timeSinceLastReference > this.timeInMilisBeforeClose) {
			NativeImage nativeImage = this.getPixels();
			if (nativeImage != null) nativeImage.close();
			this.closed = true;
		}
	}
}
