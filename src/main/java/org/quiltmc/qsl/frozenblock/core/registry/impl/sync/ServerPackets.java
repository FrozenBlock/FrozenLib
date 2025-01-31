/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
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

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocolDef;

/**
 * Identifiers of packets sent by server.
 */
@ApiStatus.Internal
public final class ServerPackets {
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
		public static final StreamCodec<FriendlyByteBuf, Handshake> CODEC = StreamCodec.ofMember(Handshake::write, Handshake::new);

		public Handshake(@NotNull FriendlyByteBuf buf) {
			this(buf.readIntIdList());
		}

		public void write(@NotNull FriendlyByteBuf buf) {
			buf.writeIntIdList(this.supportedVersions);
		}

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
		public static final StreamCodec<FriendlyByteBuf, End> CODEC = StreamCodec.ofMember(End::write, End::new);

		public End(FriendlyByteBuf buf) {
			this();
		}

		public void write(FriendlyByteBuf buf) {
		}

		@Override
		@NotNull
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
	 *
	 * }
	 * </code></pre>
	 */
	public record ErrorStyle(Component errorHeader, Component errorFooter, boolean showError) implements CustomPacketPayload {
		public static final Type<ErrorStyle> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/error_style"));
		public static final StreamCodec<FriendlyByteBuf, ErrorStyle> CODEC = StreamCodec.ofMember(ErrorStyle::write, ErrorStyle::new);

		public ErrorStyle(@NotNull FriendlyByteBuf buf) {
			this(ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buf), ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buf), buf.readBoolean());
		}

		public void write(@NotNull FriendlyByteBuf buf) {
			ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buf, this.errorHeader);
			ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buf, this.errorFooter);
			buf.writeBoolean(this.showError);
		}

		@Override
		@NotNull
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
	public record ModProtocol(String prioritizedId, Collection<ModProtocolDef> protocols) implements CustomPacketPayload {
		public static final Type<ModProtocol> PACKET_TYPE = new Type<>(ServerPackets.id("registry_sync/mod_protocol"));
		public static final StreamCodec<FriendlyByteBuf, ModProtocol> CODEC = StreamCodec.ofMember(ModProtocol::write, ModProtocol::new);

		public ModProtocol(@NotNull FriendlyByteBuf buf) {
			this(buf.readUtf(), buf.readList(ModProtocolDef::read));
		}

		public void write(@NotNull FriendlyByteBuf buf) {
			buf.writeUtf(this.prioritizedId);
			buf.writeCollection(this.protocols, ModProtocolDef::write);
		}

		@Override
		@NotNull
		public Type<?> type() {
			return PACKET_TYPE;
		}
	}

	private static ResourceLocation id(String path) {
		return FrozenSharedConstants.id(path);
	}
}
