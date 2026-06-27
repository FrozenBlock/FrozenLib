package net.frozenblock.lib.platform.service;

import net.frozenblock.lib.platform.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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

	@ClientOnly
	<P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientPayloadHandler<P> handler
	);

	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

	@ClientOnly
	void sendToServer(CustomPacketPayload payload);

	@FunctionalInterface
	interface ServerPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, MinecraftServer server, ServerPlayer player);
	}

	@ClientOnly
	@FunctionalInterface
	interface ClientPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, Minecraft minecraft, LocalPlayer player);
	}
}
