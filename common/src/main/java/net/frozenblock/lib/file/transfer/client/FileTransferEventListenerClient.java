/*
 * Copyright (C) 2026 FrozenBlock
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

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.file.transfer.FileTransferEvents;
import net.frozenblock.lib.platform.ModLoader;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
@ApiStatus.Internal
@ClientOnly
public final class FileTransferEventListenerClient {

	public static void init() {
		if (FrozenLibConstants.DEBUG_FILE_TRANSFER || ModLoader.isDevelopmentEnvironment()) {
			FileTransferEvents.FILE_RECEIVE.register(((destinationPath, fileName, file, client) -> {
				if (client) triggerToast(FileTransferToast.ToastId.TRANSFER_RECEIVE, destinationPath, fileName);
			}));

			FileTransferEvents.REQUEST_RECEIVE.register(((destinationPath, fileName, possibleExtensions, client) -> {
				if (client) triggerToast(FileTransferToast.ToastId.REQUEST_RECEIVE, destinationPath, fileName);
			}));

			FileTransferEvents.TRANSFER_FAIL.register(((destinationPath, fileName, selfInflicted, client) -> {
				if (client && !selfInflicted) triggerToast(FileTransferToast.ToastId.TRANSFER_FAIL, destinationPath, fileName);
			}));

			FileTransferEvents.REQUEST_FAIL.register(((destinationPath, fileName, selfInflicted, client) -> {
				if (client && !selfInflicted) triggerToast(FileTransferToast.ToastId.REQUEST_FAIL, destinationPath, fileName);
			}));
		}

		FileTransferEvents.FILE_RECEIVE.register(((destinationPath, fileName, file, client) -> {
			if (client) Minecraft.getInstance().frozenLib$serverTextureManager().onFileDownloaded(destinationPath, fileName);
		}));

		FileTransferEvents.REQUEST_FAIL.register(((destinationPath, fileName, selfInflicted, client) -> {
			if (client) Minecraft.getInstance().frozenLib$serverTextureManager().onFileDownloadFailed(destinationPath, fileName);
		}));

		FileTransferEvents.ILLEGAL_TRANSFER_RECEIVE.register(((destinationPath, fileName, client) -> {
			if (client) triggerToast(FileTransferToast.ToastId.ILLEGAL, destinationPath, fileName);
		}));

		FileTransferEvents.ILLEGAL_REQUEST_RECEIVE.register(((destinationPath, fileName, possibleExtensions, client) -> {
			if (client) triggerToast(FileTransferToast.ToastId.ILLEGAL, destinationPath, fileName);
		}));
	}

	private static void triggerToast(FileTransferToast.ToastId toastId, String path, String fileName) {
		FileTransferToast.addOrAppendIfNotPresent(Minecraft.getInstance().gui.toastManager(), toastId, path, fileName);
	}
}
