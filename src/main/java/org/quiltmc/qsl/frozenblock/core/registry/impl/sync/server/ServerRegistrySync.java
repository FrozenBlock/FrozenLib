/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
import java.util.function.Consumer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ServerPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol.ModProtocolDef;

@ApiStatus.Internal
public final class ServerRegistrySync {
	private static final int MAX_SAFE_PACKET_SIZE = 734003;

	public static Component noRegistrySyncMessage = Component.empty();
	public static Component errorStyleHeader = Component.empty();
	public static Component errorStyleFooter = Component.empty();
	public static boolean forceDisable = false;
	public static boolean showErrorDetails = false;

	public static IntList SERVER_SUPPORTED_PROTOCOL = new IntArrayList(ProtocolVersions.IMPL_SUPPORTED_VERSIONS);

	public static void registerHandlers() {
		ServerConfigurationConnectionEvents.CONFIGURE.register(((handler, server) -> {
			// You must check to see if the client can handle your config task
			if (ServerConfigurationNetworking.canSend(handler, ServerPackets.Handshake.PACKET_TYPE)) {
				handler.addTask(new QuiltSyncTask(handler, handler.connection));
			} else {
				handler.disconnect(ServerRegistrySync.noRegistrySyncMessage);
			}
		}));
		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.Handshake.PACKET_TYPE, ServerRegistrySync::handleHandshake);
		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.ModProtocol.ID, ServerRegistrySync::handleModProtocol);
		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.End.ID, ServerRegistrySync::handleEnd);
	}

	public static void handleHandshake(ClientPackets.Handshake handshake, ServerConfigurationPacketListenerImpl handler, PacketSender sender) {
		((SyncTaskHolder) handler).frozenLib$getQuiltSyncTask().handleHandshake(handshake);
	}

	public static void handleModProtocol(MinecraftServer server, ServerConfigurationPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
		((SyncTaskHolder) handler).frozenLib$getQuiltSyncTask().handleModProtocol(new ClientPackets.ModProtocol(buf));
	}

	public static void handleEnd(MinecraftServer server, ServerConfigurationPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
		((SyncTaskHolder) handler).frozenLib$getQuiltSyncTask().handleEnd(new ClientPackets.End(buf));
	}

	private static Component text(String string) {
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

	public static void sendSyncPackets(PacketSender sender, int syncVersion) {
		sendErrorStylePacket(sender);

		if (ModProtocol.enabled) {
			sendModProtocol(sender);
		}

		sender.sendPacket(new ServerPackets.End());
	}

	public static void sendHelloPacket(PacketSender sender) {
		sender.sendPacket(new ServerPackets.Handshake(SERVER_SUPPORTED_PROTOCOL));
	}

	public static void sendModProtocol(PacketSender sender) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUtf(ModProtocol.prioritizedId);
		buf.writeCollection(ModProtocol.ALL, ModProtocolDef::write);

		sender.sendPacket(ServerPackets.ModProtocol.ID, buf);
	}

	private static void sendErrorStylePacket(PacketSender sender) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeComponent(errorStyleHeader);
		buf.writeComponent(errorStyleFooter);
		buf.writeBoolean(showErrorDetails);

		sender.sendPacket(ServerPackets.ErrorStyle.ID, buf);
	}
}
