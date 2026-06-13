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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Used to both request and transfer files between both the client and server.
 *
 * @param transferPath The directory containing the wanted file.
 * @param fileName The name of the wanted file, including the file extension if transferring, excluding the file extension if requesting.
 * @param fileExtensions The names of the wanted file's possible extensions.
 * @param request Whether this is for a file request or not. If true, will cause a second transfer packet to be sent back in response with the file if possible.
 * @param data The data to be transferred.
 */
public record FileTransferPacket(String transferPath, String fileName, List<String> fileExtensions, boolean request, byte[] data) implements CustomPacketPayload {
	public static final String LOCAL_SOURCE = ".local";
	private static final byte[] EMPTY_DATA = new byte[0];
	@ApiStatus.Internal
	public static final Type<FileTransferPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("file_transfer"));
	@ApiStatus.Internal
	public static final StreamCodec<FriendlyByteBuf, FileTransferPacket> STREAM_CODEC = StreamCodec.ofMember(FileTransferPacket::write, FileTransferPacket::create);
	public static final int MAX_SIZE_PER_TRANSFER = 1835008; // 1.75MB

	@ApiStatus.Internal
	public static FileTransferPacket create(FriendlyByteBuf buf) {
		return new FileTransferPacket(buf.readUtf(), buf.readUtf(), buf.readList(ByteBufCodecs.STRING_UTF8), buf.readBoolean(), buf.readByteArray());
	}

	/**
	 * Creates a file transfer packet.
	 *
	 * @param destPath The path inside Minecraft's directory to send the file to.
	 * @param file The file to be sent.
	 * @return A {@link List} of new file transfer packets.
	 * @throws IOException if file reading fails.
	 */
	@Unmodifiable
	public static FileTransferPacket create(String destPath, File file) throws IOException {
		final byte[] data = readFile(file);
		return new FileTransferPacket(destPath, file.getName(), List.of(), false, data);
	}

	/**
	 * Create a file request packet.
	 *
	 * @param requestPath The path inside Minecraft's directory the requested file should be located.
	 * @param fileName The requested file's name, excluding the file extension.
	 * @param fileExtensions The possible file extensions of the requested file.
	 * @return The new file request packet.
	 */
	public static FileTransferPacket createRequest(String requestPath, String fileName, List<String> fileExtensions) {
		return new FileTransferPacket(requestPath, fileName, fileExtensions, true, EMPTY_DATA);
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

	/**
	 * Sends a file to a given player.
	 *
	 * <p> This will fail if the server's file transfer config option is disabled.
	 *
	 * @param file     the file to send.
	 * @param destPath The path inside Minecraft's directory to send the file to.
	 * @param player   The {@link ServerPlayer} to send the file to.
	 * @throws IOException
	 */
	public static void sendToPlayer(File file, String destPath, ServerPlayer player) throws IOException {
		if (!FrozenLibConfig.FILE_TRANSFER_SERVER.get()) return;
		ServerPlayNetworking.send(player, create(destPath, file));
	}

	@ApiStatus.Internal
	private void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.transferPath);
		buf.writeUtf(this.fileName);
		ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, this.fileExtensions);
		buf.writeBoolean(this.request);
		buf.writeByteArray(this.data);
	}

	@ApiStatus.Internal
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}

