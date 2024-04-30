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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking.Context;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

	public static void registerHandlers() {
		ServerConfigurationConnectionEvents.CONFIGURE.register(((handler, server) -> {
			// You must check to see if the client can handle your config task
			if (
				ServerConfigurationNetworking.canSend(handler, ServerPackets.Handshake.PACKET_TYPE)
				&& ServerConfigurationNetworking.canSend(handler, ServerPackets.ErrorStyle.PACKET_TYPE)
				&& ServerConfigurationNetworking.canSend(handler, ServerPackets.ModProtocol.PACKET_TYPE)
				&& ServerConfigurationNetworking.canSend(handler, ServerPackets.End.PACKET_TYPE)
			) {
				handler.addTask(new QuiltSyncTask(handler, handler.connection));
			}
		}));
		var registryClient = PayloadTypeRegistry.configurationC2S();
		registryClient.register(ClientPackets.Handshake.PACKET_TYPE, ClientPackets.Handshake.CODEC);
		registryClient.register(ClientPackets.ModProtocol.PACKET_TYPE, ClientPackets.ModProtocol.CODEC);
		registryClient.register(ClientPackets.End.PACKET_TYPE, ClientPackets.End.CODEC);

		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.Handshake.PACKET_TYPE, ServerRegistrySync::handleHandshake);
		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.ModProtocol.PACKET_TYPE, ServerRegistrySync::handleModProtocol);
		ServerConfigurationNetworking.registerGlobalReceiver(ClientPackets.End.PACKET_TYPE, ServerRegistrySync::handleEnd);

		var registry = PayloadTypeRegistry.configurationS2C();
		registry.register(ServerPackets.Handshake.PACKET_TYPE, ServerPackets.Handshake.CODEC);
		registry.register(ServerPackets.ModProtocol.PACKET_TYPE, ServerPackets.ModProtocol.CODEC);
		registry.register(ServerPackets.End.PACKET_TYPE, ServerPackets.End.CODEC);
		registry.register(ServerPackets.ErrorStyle.PACKET_TYPE, ServerPackets.ErrorStyle.CODEC);
	}

	public static void handleHandshake(ClientPackets.Handshake handshake, Context ctx) {
		((QuiltSyncTask) ctx.networkHandler().currentTask).handleHandshake(handshake);
	}

	public static void handleModProtocol(ClientPackets.ModProtocol modProtocol, Context ctx) {
		((QuiltSyncTask) ctx.networkHandler().currentTask).handleModProtocol(modProtocol, ctx.responseSender());
	}

	public static void handleEnd(ClientPackets.End end, Context ctx) {
		((QuiltSyncTask) ctx.networkHandler().currentTask).handleEnd(end);
	}

	public static Component text(String string) {
		if (string == null || string.isEmpty()) {
			return Component.empty();
		}

		Component text = null;
		try {
			text = Component.Serializer.fromJson(string, RegistryAccess.EMPTY);
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
		sender.sendPacket(new ServerPackets.ModProtocol(ModProtocol.prioritizedId, ModProtocol.ALL));
	}

	private static void sendErrorStylePacket(PacketSender sender) {
		sender.sendPacket(new ServerPackets.ErrorStyle(errorStyleHeader, errorStyleFooter, showErrorDetails));
	}
}
