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

package net.frozenblock.lib.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationPacketListenerImpl;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.networking.ConfigPacketSender;
import net.frozenblock.lib.platform.service.NetworkingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class FabricNetworkingHelper implements NetworkingHelper {

	@Override
	public <P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		PayloadTypeRegistry.clientboundPlay().register(type, codec);
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		PayloadTypeRegistry.serverboundPlay().register(type, codec);
	}

	@Override
	public <P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		PayloadTypeRegistry.clientboundPlay().registerLarge(type, codec, maxSize);
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		PayloadTypeRegistry.serverboundPlay().registerLarge(type, codec, maxSize);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		ServerPayloadHandler<P> handler
	) {
		ServerPlayNetworking.registerGlobalReceiver(type, (packet, ctx) ->
			handler.receive(packet, ctx.server(), ctx.player())
		);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientPayloadHandler<P> handler
	) {
		ClientPlayNetworking.registerGlobalReceiver(type, (packet, ctx) ->
			handler.receive(packet, ctx.client(), ctx.player())
		);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		ServerPlayNetworking.send(player, payload);
	}

	@Override
	public void sendToServer(CustomPacketPayload payload) {
		ClientPlayNetworking.send(payload);
	}

	@Override
	public <P extends CustomPacketPayload> void registerClientboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		PayloadTypeRegistry.clientboundConfiguration().register(type, codec);
	}

	@Override
	public <P extends CustomPacketPayload> void registerServerboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		PayloadTypeRegistry.serverboundConfiguration().register(type, codec);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalServerConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ServerConfigPayloadHandler<P> handler
	) {
		ServerConfigurationNetworking.registerGlobalReceiver(type, (payload, ctx) ->
			handler.receive(payload, ctx.packetListener(), wrapFabricSender(ctx.responseSender()))
		);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ClientConfigPayloadHandler<P> handler
	) {
		ClientConfigurationNetworking.registerGlobalReceiver(type, (payload, ctx) ->
			handler.receive(payload, ctx.client(), wrapFabricSender(ctx.responseSender()))
		);
	}

	@Override
	public boolean canSendConfigPacket(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> type) {
		return ServerConfigurationNetworking.canSend(handler, type);
	}

	@Override
	public ConfigPacketSender getServerConfigSender(ServerConfigurationPacketListenerImpl handler) {
		return wrapFabricSender(ServerConfigurationNetworking.getSender(handler));
	}

	@Override
	public void completeConfigTask(ServerConfigurationPacketListenerImpl handler, ConfigurationTask.Type type) {
		handler.completeTask(type);
	}

	private static ConfigPacketSender wrapFabricSender(PacketSender fabricSender) {
		return new ConfigPacketSender() {
			@Override
			public void sendPacket(CustomPacketPayload payload) {
				fabricSender.sendPacket(payload);
			}

			@Override
			public void disconnect(Component reason) {
				fabricSender.disconnect(reason);
			}
		};
	}
}
