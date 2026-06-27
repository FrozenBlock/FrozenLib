package net.frozenblock.lib.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.platform.service.NetworkingHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

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
}
