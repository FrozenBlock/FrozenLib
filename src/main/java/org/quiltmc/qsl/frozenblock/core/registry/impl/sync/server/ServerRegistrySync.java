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

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ServerPackets;

@ApiStatus.Internal
public final class ServerRegistrySync {
	private static final int MAX_SAFE_PACKET_SIZE = 734003;

	public static Component noRegistrySyncMessage = Component.empty();
	public static Component errorStyleHeader = Component.empty();
	public static Component errorStyleFooter = Component.empty();
	public static boolean forceDisable = false;
	public static boolean showErrorDetails = true;

	public static IntList SERVER_SUPPORTED_PROTOCOL = new IntArrayList(ProtocolVersions.IMPL_SUPPORTED_VERSIONS);

	public static Component text(String string) {
		if (string == null || string.isEmpty()) {
			return Component.empty();
		}

		Component text = null;
		try {
			text = Component.Serializer.fromJson(string);
		} catch (Exception ignored) {}

		return text != null ? text : Component.literal(string);
	}

	public static boolean isNamespaceVanilla(String namespace) {
		return namespace.equals(ResourceLocation.DEFAULT_NAMESPACE) || namespace.equals("brigadier");
	}

	public static boolean shouldSync() {
		if (forceDisable) {
			return false;
		}

        return ModProtocol.enabled;
    }

	public static boolean requiresSync() {
		if (forceDisable) {
			return false;
		}

		if (!ModProtocol.REQUIRED.isEmpty()) {
			return true;
		}

		return false;
	}

	public static void sendSyncPackets(Connection sender, ServerPlayer player, int syncVersion) {
		sendErrorStylePacket(sender);

		if (ModProtocol.enabled) {
			sendModProtocol(sender);
		}

		sender.send(ServerPlayNetworking.createS2CPacket(ServerPackets.End.PACKET_TYPE.getId(), PacketByteBufs.empty()));
	}

	public static void sendHelloPacket(Connection sender) {
		var buf = PacketByteBufs.create();
		new ServerPackets.Handshake(SERVER_SUPPORTED_PROTOCOL).write(buf);
		sender.send(ServerPlayNetworking.createS2CPacket(ServerPackets.Handshake.PACKET_TYPE.getId(), buf));
	}

	public static void sendModProtocol(Connection sender) {
		var buf = PacketByteBufs.create();
		new ServerPackets.ModProtocol(ModProtocol.prioritizedId, ModProtocol.ALL).write(buf);
		sender.send(ServerPlayNetworking.createS2CPacket(ServerPackets.ModProtocol.PACKET_TYPE.getId(), buf));
	}

	private static void sendErrorStylePacket(Connection sender) {
		var buf = PacketByteBufs.create();
		new ServerPackets.ErrorStyle(errorStyleHeader, errorStyleFooter, showErrorDetails).write(buf);
		sender.send(ServerPlayNetworking.createS2CPacket(ServerPackets.ErrorStyle.PACKET_TYPE.getId(), buf));
	}
}
