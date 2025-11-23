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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Used to both request and transfer files between both the client and server.
 *
 * @param transferPath The directory containing the wanted file.
 * @param fileName The name of the wanted file, including the file extension.
 * @param request Whether this is for a file request or not. If true, will cause a second transfer packet to be sent back in response with the file if possible.
 * @param snippet The raw data being transferred over the packet, as well as its index (used when split into multiple packets.) Will be ignored if this is a request.
 * @param totalPacketCount The total amount of packets to be sent for the file transfer. Will be ignored if this is a request.
 */
public record FileTransferPacket(String transferPath, String fileName, boolean request, FileTransferSnippet snippet, int totalPacketCount) implements CustomPacketPayload {
	@ApiStatus.Internal
	public static final Type<FileTransferPacket> PACKET_TYPE = new Type<>(FrozenLibConstants.id("file_transfer"));
	@ApiStatus.Internal
	public static final StreamCodec<FriendlyByteBuf, FileTransferPacket> STREAM_CODEC = StreamCodec.ofMember(FileTransferPacket::write, FileTransferPacket::create);
	private static final int MAX_BYTES_PER_TRANSFER = 1835008; // 1.75MB

	@ApiStatus.Internal
	public static FileTransferPacket create(FriendlyByteBuf buf) {
		return new FileTransferPacket(buf.readUtf(), buf.readUtf(), buf.readBoolean(), FileTransferSnippet.read(buf), buf.readVarInt());
	}

	/**
	 * Creates a {@link List} of file transfer packets.
	 *
	 * @param destPath The path inside Minecraft's directory to send the file to.
	 * @param file The file to be sent.
	 * @return A {@link List} of new file transfer packets.
	 * @throws IOException
	 */
	@Unmodifiable
	public static List<FileTransferPacket> create(String destPath, File file) throws IOException {
		final Pair<Integer, List<FileTransferSnippet>> snippets = createSnippets(readFile(file));
		final int totalPacketCount = snippets.getFirst();

		final ArrayList<FileTransferPacket> packets = new ArrayList<>();
		for (FileTransferSnippet snippet : snippets.getSecond()) {
			packets.add(
				new FileTransferPacket(
					destPath,
					file.getName(),
					false,
					snippet,
					totalPacketCount
				)
			);
		}

		return ImmutableList.copyOf(packets);
	}

	/**
	 * Create a file request packet.
	 *
	 * @param requestPath The path inside Minecraft's directory the requested file should be located.
	 * @param fileName    The requested file's name, including the file extension.
	 * @return The new file request packet.
	 */
	public static FileTransferPacket createRequest(String requestPath, String fileName) {
		return new FileTransferPacket(requestPath, fileName, true, FileTransferSnippet.EMPTY, 0);
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
		if (!FrozenLibConfig.FILE_TRANSFER_SERVER) return;
		for (FileTransferPacket packet : create(destPath, file)) ServerPlayNetworking.send(player, packet);
	}

	@ApiStatus.Internal
	private void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.transferPath);
		buf.writeUtf(this.fileName);
		buf.writeBoolean(this.request);
		this.snippet.write(buf);
		buf.writeVarInt(this.totalPacketCount);
	}

	@ApiStatus.Internal
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return PACKET_TYPE;
	}

	private static Pair<Integer, List<FileTransferSnippet>> createSnippets(byte[] bytes) {
		final AtomicInteger index = new AtomicInteger(0);
		final List<FileTransferSnippet> snippets = new ArrayList<>();

		Lists.partition(Arrays.asList(ArrayUtils.toObject(bytes)), MAX_BYTES_PER_TRANSFER).forEach(byteChunk -> {
			snippets.add(
				new FileTransferSnippet(
					ArrayUtils.toPrimitive(byteChunk.toArray(new Byte[0])),
					index.incrementAndGet()
				)
			);
		});

		return Pair.of(index.get(), snippets);
	}

	public record FileTransferSnippet(byte[] bytes, int index) {
		public static final FileTransferSnippet EMPTY = new FileTransferSnippet(new byte[0], 0);

		@Contract("_ -> new")
		public static FileTransferSnippet read(FriendlyByteBuf byteBuf) {
			return new FileTransferSnippet(byteBuf.readByteArray(), byteBuf.readVarInt());
		}

		@Contract("_ -> param1")
		public ByteBuf write(FriendlyByteBuf byteBuf) {
			byteBuf.writeByteArray(this.bytes);
			byteBuf.writeVarInt(this.index);
			return byteBuf;
		}
	}
}

