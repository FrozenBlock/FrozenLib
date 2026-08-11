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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocolDef;

/**
 * Identifiers of packets sent by server.
 */
@ApiStatus.Internal
public final class ServerPackets {

	private static Identifier id(String path) {
		return FrozenLibConstants.id(path);
	}

	/**
	 * Starts registry sync.
	 *
	 * <pre><code>
	 * {
	 *   Supported Versions: IntList
	 * }
	 * </code></pre>
	 */
	public record Handshake(IntList supportedVersions) implements CustomPacketPayload {
		public static final Type<Handshake> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/handshake"));
		public static final StreamCodec<FriendlyByteBuf, Handshake> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.collection(IntArrayList::new)),
			Handshake::supportedVersions,
			Handshake::new
		);

		@Override
		public Type<Handshake> type() {
			return PACKET_TYPE;
		}
	}

	/**
	 * Ends registry sync. No data
	 */
	public record End() implements CustomPacketPayload {
		public static final Type<End> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/end"));
		public static final StreamCodec<FriendlyByteBuf, End> CODEC = StreamCodec.unit(new End());

		@Override
		public Type<?> type() {
			return PACKET_TYPE;
		}
	}

	/**
	 * This packet sets failure text look/properties.
	 * Requires protocol version 3 or newer.
	 *
	 * <pre><code>
	 * {
	 *   Text Header: Text (String)
	 *   Text Footer: Text (String)
	 *   Show Details: bool
	 * }
	 * </code></pre>
	 */
	public record ErrorStyle(Component errorHeader, Component errorFooter, boolean showError) implements CustomPacketPayload {
		public static final Type<ErrorStyle> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/error_style"));
		public static final StreamCodec<FriendlyByteBuf, ErrorStyle> CODEC = StreamCodec.composite(
			ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC, ErrorStyle::errorHeader,
			ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC, ErrorStyle::errorFooter,
			ByteBufCodecs.BOOL, ErrorStyle::showError,
			ErrorStyle::new
		);

		@Override
		public Type<?> type() {
			return PACKET_TYPE;
		}
	}

	/**
	 * This packet requests client to validate and return supported Mod Protocol versions.
	 *
	 * <pre><code>
	 * {
	 *   Prioritized Id: String
	 *   Count of Entries: VarInt
	 *   [
	 *     Id: String
	 *     Name: String
	 *     Supported Versions: IntList
	 *     Optional: boolean
	 *   ]
	 * }
	 * </code></pre>
	 */
	public record ModProtocol(String prioritizedId, List<ModProtocolDef> protocols) implements CustomPacketPayload {
		public static final Type<ModProtocol> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/mod_protocol"));
		public static final StreamCodec<FriendlyByteBuf, ModProtocol> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ModProtocol::prioritizedId,
			ModProtocolDef.STREAM_CODEC.apply(ByteBufCodecs.list()), ModProtocol::protocols,
			ModProtocol::new
		);

		public Type<?> type() {
			return PACKET_TYPE;
		}
	}

}
