/*
 * Copyright 2022 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Identifiers of packets sent by server.
 */
@ApiStatus.Internal
public final class ClientPackets {
	/**
	 * Response for {@link ServerPackets.Handshake#ID}. Selects the registry sync version to be used from the server's supported options.
	 *
	 * <pre><code>
	 * {
	 *     Supported Version: VarInt
	 * }
	 * </code></pre>
	 */
	public record Handshake(int version) implements FabricPacket {
		public static final PacketType<Handshake> PACKET_TYPE = PacketType.create(ClientPackets.id("registry_sync/handshake_client"), Handshake::new);

		public Handshake(FriendlyByteBuf buf) {
			this(
				buf.readVarInt()
			);
		}

		@Override
		public void write(FriendlyByteBuf buf) {
			buf.writeVarInt(this.version);
		}

		@Override
		public PacketType<Handshake> getType() {
			return PACKET_TYPE;
		}
	}

	/**
	 * Sent after receiving Mod Protocol request packet from server.
	 * Returns all latest supported by client version of requested Mod Protocols see {@link ServerPackets.ModProtocol#ID}
	 *
	 * <pre><code>
	 * {
	 *   Count of Entries: VarInt
	 *   [
	 *     Id: String
	 *     Highest Supported Version: VarInt
	 *   ]
	 * }
	 * </code></pre>
	 */
	public record ModProtocol(Object2IntOpenHashMap<String> protocols) implements CustomPacketPayload {
		public static final ResourceLocation ID = ClientPackets.id("registry_sync/mod_protocol");

		public ModProtocol(FriendlyByteBuf buf) {
			this(read(buf));
		}

		private static Object2IntOpenHashMap<String> read(FriendlyByteBuf buf) {
			Object2IntOpenHashMap<String> protocols = new Object2IntOpenHashMap<>();

			int count = buf.readVarInt();

			while (count-- > 0) {
				protocols.put(buf.readUtf(), buf.readVarInt());
			}

			return protocols;
		}

		@Override
		public void write(FriendlyByteBuf buf) {
			buf.writeVarInt(this.protocols.size());
			for (var entry : this.protocols.object2IntEntrySet()) {
				buf.writeUtf(entry.getKey());
				buf.writeVarInt(entry.getIntValue());
			}
		}

		@Override
		public ResourceLocation id() {
			return ID;
		}
	}

	/**
	 * Ends registry sync. No data
	 */
	public record End() implements CustomPacketPayload {
		public static final ResourceLocation ID = ClientPackets.id("registry_sync/end");

		public End(FriendlyByteBuf buf) {
			this();
		}

		@Override
		public void write(FriendlyByteBuf buf) {
		}

		@Override
		public ResourceLocation id() {
			return ID;
		}
	}

	private static ResourceLocation id(String path) {
		return FrozenMain.id(path);
	}
}
