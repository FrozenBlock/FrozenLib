/*
 * Copyright 2023-2024 The Quilt Project
 * Copyright 2023-2024 FrozenBlock
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
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenSharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
	public record Handshake(IntList supportedVersions) implements FabricPacket {
		public static final PacketType<Handshake> PACKET_TYPE = PacketType.create(ServerPackets.id("registry_sync/handshake"), Handshake::new);

		public Handshake(@NotNull FriendlyByteBuf buf) {
			this(buf.readIntIdList());
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buf) {
			buf.writeIntIdList(this.supportedVersions);
		}

		@Override
		public PacketType<Handshake> getType() {
			return PACKET_TYPE;
		}
	}

	/**
	 * Ends registry sync. No data
	 */
	public record End() implements FabricPacket {
		public static final PacketType<End> PACKET_TYPE = PacketType.create(ServerPackets.id("registry_sync/end"), End::new);

		public End(FriendlyByteBuf buf) {
			this();
		}

		@Override
		public void write(FriendlyByteBuf buf) {
		}

		@Override
		public PacketType<?> getType() {
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
	public record ErrorStyle(Component errorHeader, Component errorFooter, boolean showError) implements FabricPacket {
		public static final PacketType<ErrorStyle> PACKET_TYPE = PacketType.create(ServerPackets.id("registry_sync/error_style"), ErrorStyle::new);

		public ErrorStyle(@NotNull FriendlyByteBuf buf) {
			this(buf.readComponent(), buf.readComponent(), buf.readBoolean());
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buf) {
			buf.writeComponent(this.errorHeader);
			buf.writeComponent(this.errorFooter);
			buf.writeBoolean(this.showError);
		}

		@Override
		public PacketType<?> getType() {
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
	public record ModProtocol(String prioritizedId, Collection<ModProtocolDef> protocols) implements FabricPacket {
		public static final PacketType<ModProtocol> PACKET_TYPE = PacketType.create(ServerPackets.id("registry_sync/mod_protocol"), ModProtocol::new);

		public ModProtocol(@NotNull FriendlyByteBuf buf) {
			this(buf.readUtf(), buf.readList(ModProtocolDef::read));
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buf) {
			buf.writeUtf(this.prioritizedId);
			buf.writeCollection(this.protocols, ModProtocolDef::write);
		}

		@Override
		public PacketType<?> getType() {
			return PACKET_TYPE;
		}
	}

	private static ResourceLocation id(String path) {
		return FrozenSharedConstants.id(path);
	}
}
