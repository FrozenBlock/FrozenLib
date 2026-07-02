/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.platform.service;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.networking.ConfigPacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public interface NetworkingHelper {

	<P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	);

	<P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	);

	<P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	);

	<P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	);

	<P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		ServerPayloadHandler<P> handler
	);

	@Environment(EnvType.CLIENT)
	<P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientPayloadHandler<P> handler
	);

	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

	@Environment(EnvType.CLIENT)
	void sendToServer(CustomPacketPayload payload);

	<P extends CustomPacketPayload> void registerClientboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	);

	<P extends CustomPacketPayload> void registerServerboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	);

	<P extends CustomPacketPayload> void registerGlobalServerConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ServerConfigPayloadHandler<P> handler
	);

	@Environment(EnvType.CLIENT)
	<P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ClientConfigPayloadHandler<P> handler
	);

	boolean canSendConfigPacket(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> type);

	ConfigPacketSender getServerConfigSender(ServerConfigurationPacketListenerImpl handler);

	void completeConfigTask(ServerConfigurationPacketListenerImpl handler, ConfigurationTask.Type type);

	@FunctionalInterface
	interface ServerPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, MinecraftServer server, ServerPlayer player);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	interface ClientPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, Minecraft minecraft, LocalPlayer player);
	}

	@FunctionalInterface
	interface ServerConfigPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, ServerConfigurationPacketListenerImpl listener, ConfigPacketSender sender);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	interface ClientConfigPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, Minecraft client, ConfigPacketSender sender);
	}
}
