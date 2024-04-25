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

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.client;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocolDef;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.RegistrySyncText;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ServerPackets;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.ServerRegistrySync;
import org.slf4j.Logger;


@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class ClientRegistrySync {
	private static final Logger LOGGER = LogUtils.getLogger();

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private static int syncVersion = -1;
	@SuppressWarnings("unused")
	private static int currentCount;
	@SuppressWarnings("unused")
	private static byte currentFlags;

	private static Component errorStyleHeader = ServerRegistrySync.errorStyleHeader;
	private static Component errorStyleFooter = ServerRegistrySync.errorStyleFooter;
	private static boolean showErrorDetails = ServerRegistrySync.showErrorDetails;

	private static Component disconnectMainReason = null;

	private static LogBuilder builder = new LogBuilder();
	private static boolean mustDisconnect;

	public static void registerHandlers() {
		ClientConfigurationNetworking.registerGlobalReceiver(ServerPackets.Handshake.PACKET_TYPE.getId(), ClientRegistrySync::handleHelloPacket);
		ClientConfigurationNetworking.registerGlobalReceiver(ServerPackets.End.PACKET_TYPE.getId(), ClientRegistrySync::handleEndPacket);
		ClientConfigurationNetworking.registerGlobalReceiver(ServerPackets.ErrorStyle.PACKET_TYPE.getId(), ClientRegistrySync::handleErrorStylePacket);
		ClientConfigurationNetworking.registerGlobalReceiver(ServerPackets.ModProtocol.PACKET_TYPE.getId(), ClientRegistrySync::handleModProtocol);
	}

	private static void handleModProtocol(Minecraft client, ClientConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
		var modProtocol = new ServerPackets.ModProtocol(buf);
		var prioritizedId = modProtocol.prioritizedId();
		var protocols = modProtocol.protocols();

		var values = new Object2IntOpenHashMap<String>(protocols.size());
		var unsupportedList = new ArrayList<ModProtocolDef>();
		ModProtocolDef missingPrioritized = null;

		boolean disconnect = false;

		for (var protocol : protocols) {
			var local = ModProtocol.getVersion(protocol.id());
			var latest = protocol.latestMatchingVersion(local);
			LOGGER.info(String.valueOf(latest));
			if (latest != ProtocolVersions.NO_PROTOCOL) {
				values.put(protocol.id(), latest);
			} else if (!protocol.optional()) {
				unsupportedList.add(protocol);
				disconnect = true;
				if (prioritizedId.equals(protocol.id())) {
					missingPrioritized = protocol;
				}
			}
		}

		if (disconnect) {
			markDisconnected(RegistrySyncText.unsupportedModVersion(unsupportedList, missingPrioritized));

			builder.pushT("unsupported_protocol", "Unsupported Mod Protocol");

			for (var entry : unsupportedList) {
				builder.textEntry(Component.literal(entry.displayName()).append(Component.literal(" (" + entry.id() + ")").withStyle(ChatFormatting.DARK_GRAY)).append(" | Server: ").append(stringifyVersions(entry.versions())).append(", Client: ").append(stringifyVersions(ModProtocol.getVersion(entry.id()))));
			}
		} else {
			sendSupportedModProtocol(sender, values);
		}
	}

	private static void handleEndPacket(Minecraft client, ClientConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
		syncVersion = -1;

		if (mustDisconnect) {
			var entry = Component.empty();
			entry.append(errorStyleHeader);

			if (disconnectMainReason != null && showErrorDetails && !isTextEmpty(disconnectMainReason)) {
				entry.append("\n");
				entry.append(disconnectMainReason);
			}

			if (!isTextEmpty(errorStyleFooter)) {
				entry.append("\n");
				entry.append(errorStyleFooter);
			}

			handler.connection.disconnect(entry);

			LOGGER.warn(builder.asString());
		} else {
			sender.sendPacket(new ClientPackets.End());
		}
	}

	private static String stringifyVersions(IntList versions) {
		if (versions == null || versions.isEmpty()) {
			return "Missing!";
		}

		var b = new StringBuilder().append('[');

		var iter = versions.iterator();

		while (iter.hasNext()) {
			b.append(iter.nextInt());

			if (iter.hasNext()) {
				b.append(", ");
			}
		}

		return b.append(']').toString();
	}

	private static void sendSupportedModProtocol(PacketSender sender, Object2IntOpenHashMap<String> values) {
		sender.sendPacket(new ClientPackets.ModProtocol(values));
	}

	private static void handleErrorStylePacket(Minecraft client, ClientConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
		var errorStyle = new ServerPackets.ErrorStyle(buf);

		errorStyleHeader = errorStyle.errorHeader();
		errorStyleFooter = errorStyle.errorFooter();
		showErrorDetails = errorStyle.showError();
	}

	private static void handleHelloPacket(Minecraft client, ClientConfigurationPacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
		syncVersion = ProtocolVersions.getHighestSupportedLocal(new ServerPackets.Handshake(buf).supportedVersions());

		sender.sendPacket(new ClientPackets.Handshake(syncVersion));
		builder.clear();
	}

	private static void markDisconnected(Component reason) {
		if (disconnectMainReason == null) {
			disconnectMainReason = reason;
		}

		mustDisconnect = true;
	}

	private static boolean isTextEmpty(Component text) {
		return (text.getContents().equals(PlainTextContents.EMPTY) || (text.getContents() instanceof PlainTextContents literalContents && literalContents.text().isEmpty())) && text.getSiblings().isEmpty();
	}

	public static void disconnectCleanup(Minecraft client) {
		errorStyleHeader = ServerRegistrySync.errorStyleHeader;
		errorStyleFooter = ServerRegistrySync.errorStyleFooter;
		showErrorDetails = ServerRegistrySync.showErrorDetails;
		disconnectMainReason = null;
	}
}
