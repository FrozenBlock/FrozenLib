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

package net.frozenblock.lib.file.transfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used to both request and transfer files between both the client and server.
 *
 * @param transferPath The directory containing the wanted file.
 * @param fileName The name of the wanted file, including the file extension if transferring, excluding the file extension if requesting.
 * @param fileExtensions The names of the wanted file's possible extensions.
 * @param request Whether this is for a file request. If true, will cause a second transfer packet to be sent back in response with the file if possible.
 * @param data The data to be transferred.
 */
public record FileTransferPacket(String transferPath, String fileName, List<String> fileExtensions, boolean request, byte[] data) implements CustomPacketPayload {
	public static final String LOCAL_SOURCE = ".local";
	private static final byte[] EMPTY_DATA = new byte[0];
	@ApiStatus.Internal
	public static final Type<FileTransferPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("file_transfer"));
	@ApiStatus.Internal
	public static final StreamCodec<RegistryFriendlyByteBuf, FileTransferPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, FileTransferPacket::transferPath,
		ByteBufCodecs.STRING_UTF8, FileTransferPacket::fileName,
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), FileTransferPacket::fileExtensions,
		ByteBufCodecs.BOOL, FileTransferPacket::request,
		ByteBufCodecs.BYTE_ARRAY, FileTransferPacket::data,
		FileTransferPacket::new
	);
	public static final int MAX_SIZE_PER_TRANSFER = 1835008; // 1.75MB

	/**
	 * Creates a file transfer packet.
	 * @param destinationPath The path inside Minecraft's directory to send the file to.
	 * @param file The file to be sent.
	 * @return The new file transfer packet.
	 * @throws IOException If file reading fails.
	 */
	public static FileTransferPacket create(String destinationPath, File file) throws IOException {
		final byte[] data = readFile(file);
		return new FileTransferPacket(destinationPath, file.getName(), List.of(), false, data);
	}

	/**
	 * Create a file request packet.
	 * @param requestPath The path inside Minecraft's directory the requested file should be sent to.
	 * @param fileName The requested file's name, excluding the file extension.
	 * @param fileExtensions The possible file extensions of the requested file.
	 * @return The new file request packet.
	 */
	public static FileTransferPacket createRequest(String requestPath, String fileName, List<String> fileExtensions) {
		return new FileTransferPacket(requestPath, fileName, fileExtensions, true, EMPTY_DATA);
	}

	/**
	 * Sends a file to a given player.
	 * <p>
	 * This will fail if the server's file transfer config option is disabled.
	 * <p>
	 * This method invokes either {@link FileTransferEvents#FILE_SEND} or {@link FileTransferEvents#TRANSFER_FAIL} depending on its outcome.
	 * @param file the file to send.
	 * @param destinationPath The path inside Minecraft's directory to send the file to.
	 * @param player The {@link ServerPlayer} to send the file to.
	 * @throws IOException If file reading fails.
	 */
	public static void sendToPlayer(File file, String destinationPath, ServerPlayer player) throws IOException {
		final String fileNameWithoutExtension = FilenameUtils.removeExtension(file.getName());

		if (!FrozenLibConfig.FILE_TRANSFER_SERVER.get()) {
			FileTransferEvents.TRANSFER_FAIL.invoker().onTransferFailed(destinationPath, fileNameWithoutExtension, true, false);
			FileTransferFailPacket.sendToPlayer(fileNameWithoutExtension, destinationPath, true, player);
			return;
		}

		NetworkingHelper.sendToPlayer(player, create(destinationPath, file));
		FileTransferEvents.FILE_SEND.invoker().onFileSent(destinationPath, fileNameWithoutExtension, file, false);
	}

	@ApiStatus.Internal
	private static byte @Nullable [] readFile(File file) {
		try {
			final FileInputStream fileInputStream = new FileInputStream(file);
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			fileInputStream.transferTo(byteArrayOutputStream);
			fileInputStream.close();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@ApiStatus.Internal
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}

