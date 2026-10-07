package net.frozenblock.lib.file.transfer;

import java.io.File;
import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.Event;
import net.frozenblock.lib.event.api.EventRegistry;

@UtilityClass
public final class FileTransferEvents {
	/**
	 * The event that is triggered when a file transfer is received.
	 * <p>
	 * (Example: A file is received. Not much to explain.)
	 */
	public static final Event<FileReceive> FILE_RECEIVE = EventRegistry.createEnvironmentEvent(FileReceive.class, callbacks -> (destinationPath, fileName, file, client) -> {
		for (FileReceive callback : callbacks) {
			callback.onFileReceived(destinationPath, fileName, file, client);
		}
	});

	/**
	 * The event that is triggered when a file transfer is attempted that contains an invalid destination or file extension.
	 * <p>
	 * This should never trigger under normal circumstances. If it does, a player/mod is likely attempting an exploit.
	 */
	public static final Event<IllegalTransferReceive> ILLEGAL_TRANSFER_RECEIVE = EventRegistry.createEnvironmentEvent(IllegalTransferReceive.class, callbacks -> (destinationPath, fileName, client) -> {
		for (IllegalTransferReceive callback : callbacks) {
			callback.onIllegalTransferReceived(destinationPath, fileName, client);
		}
	});

	/**
	 * The event that is triggered when a file transfer is sent.
	 * <p>
	 * (Example: Sending a file to the recipient.)
	 */
	public static final Event<FileSend> FILE_SEND = EventRegistry.createEnvironmentEvent(FileSend.class, callbacks -> (destinationPath, fileName, file, client) -> {
		for (FileSend callback : callbacks) {
			callback.onFileSent(destinationPath, fileName, file, client);
		}
	});

	/**
	 * The event that is triggered when a file transfer fails.
	 * <p>
	 * (Example: The server attempts to send a file, but it doesn't have the file.)
	 */
	public static final Event<TransferFail> TRANSFER_FAIL = EventRegistry.createEnvironmentEvent(TransferFail.class, callbacks -> (destinationPath, fileName, selfInflicted, client) -> {
		for (TransferFail callback : callbacks) {
			callback.onTransferFailed(destinationPath, fileName, selfInflicted, client);
		}
	});

	/**
	 * The event that is triggered when a file request is received.
	 * <p>
	 * (Example: A client requests a file, and the server receives the request packet.)
	 */
	public static final Event<RequestReceive> REQUEST_RECEIVE = EventRegistry.createEnvironmentEvent(RequestReceive.class, callbacks -> (requestPath, fileName, possibleExtensions, client) -> {
		for (RequestReceive callback : callbacks) {
			callback.onRequestReceived(requestPath, fileName, possibleExtensions, client);
		}
	});

	/**
	 * The event that is triggered when a file request is received that contains an invalid request path or file extension.
	 * <p>
	 * This should never trigger under normal circumstances. If it does, a player/mod is likely attempting an exploit.
	 */
	public static final Event<IllegalRequestReceive> ILLEGAL_REQUEST_RECEIVE = EventRegistry.createEnvironmentEvent(IllegalRequestReceive.class, callbacks -> (requestPath, fileName, possibleExtensions, client) -> {
		for (IllegalRequestReceive callback : callbacks) {
			callback.onIllegalRequestReceived(requestPath, fileName, possibleExtensions, client);
		}
	});

	/**
	 * The event that is triggered when a file request is sent.
	 * <p>
	 * (Example: The client requests a file.)
	 */
	public static final Event<RequestSend> REQUEST_SEND = EventRegistry.createEnvironmentEvent(RequestSend.class, callbacks -> (requestPath, fileName, possibleExtensions, client) -> {
		for (RequestSend callback : callbacks) {
			callback.onRequestSent(requestPath, fileName, possibleExtensions, client);
		}
	});

	/**
	 * The event that is triggered when a file request fails.
	 * <p>
	 * This also applies to files failing to be saved when received, or file transfer fail packets being received without a request first being sent.
	 * <p>
	 * (Example: The client requests a file from a server, but the server doesn't have the file.)
	 */
	public static final Event<RequestFail> REQUEST_FAIL = EventRegistry.createEnvironmentEvent(RequestFail.class, callbacks -> (destinationPath, fileName, selfInflicted, client) -> {
		for (RequestFail callback : callbacks) {
			callback.onRequestFailed(destinationPath, fileName, selfInflicted, client);
		}
	});

	/**
	 * A functional interface representing a file receive event.
	 */
	@FunctionalInterface
	public interface FileReceive {
		/**
		 * Runs when a file transfer is received.
		 * <p>
		 * (Example: A file is received. Not much to explain.)
		 * @param destinationPath The destination of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file being received, excluding the file extension.
		 * @param file The {@link File} being received.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onFileReceived(String destinationPath, String fileName, File file, boolean client);
	}

	/**
	 * A functional interface representing an illegal transfer receive event.
	 */
	@FunctionalInterface
	public interface IllegalTransferReceive {
		/**
		 * Runs when a file transfer is received that contains an invalid destination or file extension.
		 * <p>
		 * This should never trigger under normal circumstances. If it does, a player/mod is likely attempting an exploit.
		 * @param destinationPath The destination of the file within Minecraft's game directory, in text.
		 * @param fileName The file name, excluding the file extension, of the illegal file.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onIllegalTransferReceived(String destinationPath, String fileName, boolean client);
	}

	/**
	 * A functional interface representing a file send event.
	 */
	@FunctionalInterface
	public interface FileSend {
		/**
		 * Runs when a file transfer is sent.
		 * <p>
		 * (Example: Sending a file to the recipient.)
		 * @param destinationPath The destination of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file being sent, excluding the file extension.
		 * @param file The {@link File} being sent.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onFileSent(String destinationPath, String fileName, File file, boolean client);
	}

	/**
	 * A functional interface representing a transfer fail event.
	 */
	@FunctionalInterface
	public interface TransferFail {
		/**
		 * Runs when a file transfer fails.
		 * <p>
		 * (Example: The server attempts to send a file, but it doesn't have the file.)
		 * @param targetPath The assumed path of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file meant to be transferred, excluding the file extension.
		 * @param selfInflicted Whether the failure was triggered by a local condition (file transfer config options set to false.)
		 * @param client Whether this has occurred on the logical client.
		 */
		void onTransferFailed(String targetPath, String fileName, boolean selfInflicted, boolean client);
	}

	/**
	 * A functional interface representing a request receive event.
	 */
	@FunctionalInterface
	public interface RequestReceive {
		/**
		 * Runs when a file request is received.
		 * <p>
		 * (Example: A client requests a file, and the server receives the request packet.)
		 * @param requestPath The assumed location of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file being requested, excluding the file extension.
		 * @param possibleExtensions A list of possible file extensions the requested file could have.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onRequestReceived(String requestPath, String fileName, List<String> possibleExtensions, boolean client);
	}

	/**
	 * A functional interface representing an illegal request receive event.
	 */
	@FunctionalInterface
	public interface IllegalRequestReceive {
		/**
		 * Runs when a file request is received that contains an invalid request path or file extension.
		 * <p>
		 * This should never trigger under normal circumstances. If it does, a player/mod is likely attempting an exploit.
		 * @param requestPath The assumed location of the file within Minecraft's game directory, in text.
		 * @param fileName The file name, excluding the file extension, of the illegal file.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onIllegalRequestReceived(String requestPath, String fileName, List<String> possibleExtensions, boolean client);
	}

	/**
	 * A functional interface representing a request send event.
	 */
	@FunctionalInterface
	public interface RequestSend {
		/**
		 * Runs when a file request is sent.
		 * <p>
		 * (Example: The client requests a file.)
		 * @param requestPath The assumed location of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file being requested, excluding the file extension.
		 * @param possibleExtensions A list of possible file extensions the requested file could have.
		 * @param client Whether this has occurred on the logical client.
		 */
		void onRequestSent(String requestPath, String fileName, List<String> possibleExtensions, boolean client);
	}

	/**
	 * A functional interface representing a request fail event.
	 */
	@FunctionalInterface
	public interface RequestFail {
		/**
		 * Runs when a file request fails.
		 * <p>
		 * (Example: The client requests a file from a server, but the server doesn't have the file.)
		 * @param targetPath The assumed path of the file within Minecraft's game directory, in text.
		 * @param fileName The name of the file meant to be transferred, excluding the file extension.
		 * @param selfInflicted Whether the failure was triggered by a local condition (file transfer config options set to false.)
		 * @param client Whether this has occurred on the logical client.
		 */
		void onRequestFailed(String targetPath, String fileName, boolean selfInflicted, boolean client);
	}
}
