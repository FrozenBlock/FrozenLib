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

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.frozenblock.lib.event.api.events.ConfigurationConnectionEvents;
import net.frozenblock.lib.networking.ConfigPacketSender;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StrictJsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ServerPackets;
import org.quiltmc.qsl.frozenblock.core.registry.mixin.ServerConfigurationPacketListenerAccessor;

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
		ConfigurationConnectionEvents.SERVER_CONFIGURE.register((handler, server, taskAdder) -> {
			if (FrozenLibInitPlatformUtils.NETWORKING.canSendConfigPacket(handler, ServerPackets.Handshake.PACKET_TYPE)
				&& FrozenLibInitPlatformUtils.NETWORKING.canSendConfigPacket(handler, ServerPackets.ErrorStyle.PACKET_TYPE)
				&& FrozenLibInitPlatformUtils.NETWORKING.canSendConfigPacket(handler, ServerPackets.ModProtocol.PACKET_TYPE)
				&& FrozenLibInitPlatformUtils.NETWORKING.canSendConfigPacket(handler, ServerPackets.End.PACKET_TYPE)
			) {
				taskAdder.accept(new QuiltSyncTask(handler, handler.connection));
			}
		});

		FrozenLibInitPlatformUtils.NETWORKING.registerServerboundConfigPayloadType(ClientPackets.Handshake.PACKET_TYPE, ClientPackets.Handshake.CODEC);
		FrozenLibInitPlatformUtils.NETWORKING.registerServerboundConfigPayloadType(ClientPackets.ModProtocol.PACKET_TYPE, ClientPackets.ModProtocol.CODEC);
		FrozenLibInitPlatformUtils.NETWORKING.registerServerboundConfigPayloadType(ClientPackets.End.PACKET_TYPE, ClientPackets.End.CODEC);

		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalServerConfigReceiver(ClientPackets.Handshake.PACKET_TYPE,
			(payload, listener, sender) -> handleHandshake(payload, listener));
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalServerConfigReceiver(ClientPackets.ModProtocol.PACKET_TYPE,
			(payload, listener, sender) -> handleModProtocol(payload, listener, sender));
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalServerConfigReceiver(ClientPackets.End.PACKET_TYPE,
			(payload, listener, sender) -> handleEnd(payload, listener));

		FrozenLibInitPlatformUtils.NETWORKING.registerClientboundConfigPayloadType(ServerPackets.Handshake.PACKET_TYPE, ServerPackets.Handshake.CODEC);
		FrozenLibInitPlatformUtils.NETWORKING.registerClientboundConfigPayloadType(ServerPackets.ModProtocol.PACKET_TYPE, ServerPackets.ModProtocol.CODEC);
		FrozenLibInitPlatformUtils.NETWORKING.registerClientboundConfigPayloadType(ServerPackets.End.PACKET_TYPE, ServerPackets.End.CODEC);
		FrozenLibInitPlatformUtils.NETWORKING.registerClientboundConfigPayloadType(ServerPackets.ErrorStyle.PACKET_TYPE, ServerPackets.ErrorStyle.CODEC);
	}

	public static void handleHandshake(ClientPackets.Handshake handshake, net.minecraft.server.network.ServerConfigurationPacketListenerImpl listener) {
		((QuiltSyncTask) ((ServerConfigurationPacketListenerAccessor) listener).frozenLib$getCurrentTask()).handleHandshake(handshake);
	}

	public static void handleModProtocol(ClientPackets.ModProtocol modProtocol, net.minecraft.server.network.ServerConfigurationPacketListenerImpl listener, ConfigPacketSender sender) {
		((QuiltSyncTask) ((ServerConfigurationPacketListenerAccessor) listener).frozenLib$getCurrentTask()).handleModProtocol(modProtocol, sender);
	}

	public static void handleEnd(ClientPackets.End end, net.minecraft.server.network.ServerConfigurationPacketListenerImpl listener) {
		((QuiltSyncTask) ((ServerConfigurationPacketListenerAccessor) listener).frozenLib$getCurrentTask()).handleEnd(end);
	}

	public static Component text(String string) {
		if (string == null || string.isEmpty()) return Component.empty();

		Component text = null;
		try {
			final JsonElement json = StrictJsonParser.parse(string);
			text = ComponentSerialization.CODEC
				.parse(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), json)
				.resultOrPartial()
				.orElseThrow();
		} catch (Exception ignored) {}

		return text != null ? text : Component.literal(string);
	}

	public static boolean isNamespaceVanilla(String namespace) {
		return namespace.equals(Identifier.DEFAULT_NAMESPACE) || namespace.equals("brigadier");
	}

	public static boolean shouldSync() {
		if (forceDisable) return false;
		return ModProtocol.enabled;
	}

	public static boolean requiresSync() {
		if (forceDisable) return false;
		return !ModProtocol.REQUIRED.isEmpty();
	}

	public static void sendSyncPackets(ConfigPacketSender sender, int syncVersion) {
		sendErrorStylePacket(sender);
		if (ModProtocol.enabled) sendModProtocol(sender);
		sender.sendPacket(new ServerPackets.End());
	}

	public static void sendHelloPacket(ConfigPacketSender sender) {
		sender.sendPacket(new ServerPackets.Handshake(SERVER_SUPPORTED_PROTOCOL));
	}

	public static void sendModProtocol(ConfigPacketSender sender) {
		sender.sendPacket(new ServerPackets.ModProtocol(ModProtocol.prioritizedId, ModProtocol.ALL));
	}

	private static void sendErrorStylePacket(ConfigPacketSender sender) {
		sender.sendPacket(new ServerPackets.ErrorStyle(errorStyleHeader, errorStyleFooter, showErrorDetails));
	}
}
