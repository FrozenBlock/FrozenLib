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

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to notify the recipient that its requested file is not present.
 *
 * @param targetPath The intended destination of the file, from the original {@link FileTransferPacket}.
 * @param fileName The name of the wanted file, excluding the file extension, from the original {@link FileTransferPacket}.
 * @param request Whether the original {@link FileTransferPacket} was for a file request.
 */
public record FileTransferFailPacket(String targetPath, String fileName, boolean request) implements CustomPacketPayload {
	@ApiStatus.Internal
	public static final Type<FileTransferFailPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("file_transfer_fail"));
	@ApiStatus.Internal
	public static final StreamCodec<RegistryFriendlyByteBuf, FileTransferFailPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, FileTransferFailPacket::targetPath,
		ByteBufCodecs.STRING_UTF8, FileTransferFailPacket::fileName,
		ByteBufCodecs.BOOL, FileTransferFailPacket::request,
		FileTransferFailPacket::new
	);

	/**
	 * Creates a file transfer fail packet.
	 *
	 * @param targetPath The intended destination of the file, from the original {@link FileTransferPacket}.
	 * @param request Whether the original {@link FileTransferPacket} was for a file request.
	 * @return The new file transfer fail packet.
	 */
	public static FileTransferFailPacket create(String targetPath, String fileName, boolean request) {
		return new FileTransferFailPacket(targetPath, fileName, request);
	}

	/**
	 * Sends a file transfer fail packet to a player.
	 *
	 * @param fileName The name of the wanted file, excluding the file extension, from the original {@link FileTransferPacket}.
	 * @param targetPath The intended destination of the file, from the original {@link FileTransferPacket}.
	 * @param request Whether the original {@link FileTransferPacket} was for a file request.
	 * @param player The {@link ServerPlayer} to send the failure packet to.
	 */
	public static void sendToPlayer(String fileName, String targetPath, boolean request, ServerPlayer player) {
		NetworkingHelper.sendToPlayer(player, create(targetPath, fileName, request));
	}

	@ApiStatus.Internal
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}
}

