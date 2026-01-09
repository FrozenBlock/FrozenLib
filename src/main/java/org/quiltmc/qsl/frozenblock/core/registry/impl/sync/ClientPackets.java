/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
 * Modified to work on Fabric
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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

/**
 * Identifiers of packets sent by server.
 */
@ApiStatus.Internal
public final class ClientPackets {
	/**
	 * Response for {@link ServerPackets.Handshake#PACKET_TYPE}. Selects the registry sync version to be used from the server's supported options.
	 *
	 * <pre><code>
	 * {
	 *     Supported Version: VarInt
	 * }
	 * </code></pre>
	 */
	public record Handshake(int version) implements CustomPacketPayload {
		public static final Type<Handshake> PACKET_TYPE = new Type<>(FrozenLibConstants.id("registry_sync/handshake_client"));
		public static final StreamCodec<FriendlyByteBuf, Handshake> CODEC = ByteBufCodecs.VAR_INT.map(Handshake::new, Handshake::version).cast();

		@Override
		public Type<Handshake> type() {
			return PACKET_TYPE;
		}
	}

	/**
	 * Sent after receiving Mod Protocol request packet from server.
	 * Returns all latest supported by client version of requested Mod Protocols see {@link ServerPackets.ModProtocol#PACKET_TYPE}
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
		public static final Type<ModProtocol> PACKET_TYPE = new Type<>(FrozenLibConstants.id("registry_sync/mod_protocol"));
		public static final StreamCodec<FriendlyByteBuf, ModProtocol> CODEC = StreamCodec.ofMember(ModProtocol::write, ModProtocol::new);

		public ModProtocol(FriendlyByteBuf buf) {
			this(read(buf));
		}

		private static Object2IntOpenHashMap<String> read(FriendlyByteBuf buf) {
			final Object2IntOpenHashMap<String> protocols = new Object2IntOpenHashMap<>();

			int count = buf.readVarInt();
			while (count-- > 0) protocols.put(buf.readUtf(), buf.readVarInt());

			return protocols;
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeVarInt(this.protocols.size());
			for (var entry : this.protocols.object2IntEntrySet()) {
				buf.writeUtf(entry.getKey());
				buf.writeVarInt(entry.getIntValue());
			}
		}

		@Override
		public Type<ModProtocol> type() {
			return PACKET_TYPE;
		}
	}

	/**
	 * Ends registry sync. No data
	 */
	public record End() implements CustomPacketPayload {
		public static final Type<End> PACKET_TYPE = new Type<>(FrozenLibConstants.id("registry_sync/end"));
		public static final StreamCodec<FriendlyByteBuf, End> CODEC = StreamCodec.ofMember(End::write, End::new);

		public End(FriendlyByteBuf buf) {
			this();
		}

		public void write(FriendlyByteBuf buf) {
		}

		@Override
		public Type<?> type() {
			return PACKET_TYPE;
		}
	}
}
