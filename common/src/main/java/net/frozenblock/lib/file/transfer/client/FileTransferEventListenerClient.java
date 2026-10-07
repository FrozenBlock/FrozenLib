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
