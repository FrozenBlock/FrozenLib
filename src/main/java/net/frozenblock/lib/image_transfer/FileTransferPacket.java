/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.image_transfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FileTransferPacket(String transferPath, String fileName, boolean request, byte[] bytes) implements CustomPacketPayload {
    public static final Type<FileTransferPacket> PACKET_TYPE = new Type<>(
		FrozenSharedConstants.id("file_transfer")
    );
    public static final StreamCodec<FriendlyByteBuf, FileTransferPacket> STREAM_CODEC = StreamCodec.ofMember(FileTransferPacket::write, FileTransferPacket::create);

    public static @NotNull FileTransferPacket create(@NotNull FriendlyByteBuf buf) {
        return new FileTransferPacket(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readByteArray());
    }

    public static @NotNull FileTransferPacket create(String destPath, @NotNull File file) throws IOException {
        return new FileTransferPacket(destPath, file.getName(), false, readFile(file));
    }

    public static @NotNull FileTransferPacket createRequest(String directory, String fileName) {
        return new FileTransferPacket(directory, fileName, true, new byte[0]);
    }

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

    public static void sendToPlayer(File file, String transferPath, ServerPlayer player) throws IOException {
		if (!FrozenLibConfig.FILE_TRANSFER_SERVER) return;
        ServerPlayNetworking.send(player, create(transferPath, file));
    }

    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeUtf(this.transferPath);
        buf.writeUtf(this.fileName);
        buf.writeBoolean(this.request);
        buf.writeByteArray(this.bytes);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}

