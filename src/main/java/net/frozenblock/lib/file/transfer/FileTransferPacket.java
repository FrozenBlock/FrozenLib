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

package net.frozenblock.lib.file.transfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to both request and transfer files between both the client and server.
 *
 * @param transferPath The directory containing the wanted file.
 * @param fileName     The name of the wanted file, including the file extension.
 * @param request      Whether this is for a file request or not. If true, will cause a second transfer packet to be sent back in response with the file if possible.
 * @param bytes        The raw data being transferred over the packet. Will be ignored if this is a request.
 */
public record FileTransferPacket(String transferPath, String fileName, boolean request, byte[] bytes) implements CustomPacketPayload {
	@ApiStatus.Internal
	public static final Type<FileTransferPacket> PACKET_TYPE = new Type<>(
		FrozenLibConstants.id("file_transfer")
	);
	@ApiStatus.Internal
	public static final StreamCodec<FriendlyByteBuf, FileTransferPacket> STREAM_CODEC = StreamCodec.ofMember(FileTransferPacket::write, FileTransferPacket::create);

	@ApiStatus.Internal
	public static @NotNull FileTransferPacket create(@NotNull FriendlyByteBuf buf) {
		return new FileTransferPacket(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readByteArray());
	}

	/**
	 * Creates a file transfer packet.
	 *
	 * @param destPath The path inside Minecraft's directory to send the file to.
	 * @param file     The file to be sent.
	 * @return The new file transfer packet.
	 * @throws IOException
	 */
	public static @NotNull FileTransferPacket create(String destPath, @NotNull File file) throws IOException {
		return new FileTransferPacket(destPath, file.getName(), false, readFile(file));
	}

	/**
	 * Create a file request packet.
	 *
	 * @param requestPath The path inside Minecraft's directory the requested file should be located.
	 * @param fileName    The requested file's name, including the file extension.
	 * @return The new file request packet.
	 */
	public static @NotNull FileTransferPacket createRequest(String requestPath, String fileName) {
		return new FileTransferPacket(requestPath, fileName, true, new byte[0]);
	}

	@ApiStatus.Internal
	private static byte @Nullable [] readFile(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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
		if (!FrozenLibConfig.FILE_TRANSFER_SERVER) return;
		ServerPlayNetworking.send(player, create(destPath, file));
	}

	@ApiStatus.Internal
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUtf(this.transferPath);
		buf.writeUtf(this.fileName);
		buf.writeBoolean(this.request);
		buf.writeByteArray(this.bytes);
	}

	@ApiStatus.Internal
	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}

