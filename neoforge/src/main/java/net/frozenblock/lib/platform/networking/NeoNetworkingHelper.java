package net.frozenblock.lib.platform.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.platform.service.NetworkingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public class NeoNetworkingHelper implements NetworkingHelper {

	private record PayloadEntry<P extends CustomPacketPayload>(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		boolean large,
		int maxSize
	) {}

	private final List<PayloadEntry<?>> s2cEntries = new ArrayList<>();
	private final List<PayloadEntry<?>> c2sEntries = new ArrayList<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> serverHandlers = new HashMap<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> clientHandlers = new HashMap<>();

	@Override
	public <P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		s2cEntries.add(new PayloadEntry<>(type, codec, false, 0));
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		c2sEntries.add(new PayloadEntry<>(type, codec, false, 0));
	}

	@Override
	public <P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		s2cEntries.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		c2sEntries.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		ServerPayloadHandler<P> handler
	) {
		serverHandlers.put(type, (payload, context) ->
			handler.receive((P) payload, ((ServerPlayer) context.player()).server, (ServerPlayer) context.player())
		);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientPayloadHandler<P> handler
	) {
		clientHandlers.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), (LocalPlayer) context.player())
		);
	}

	@Override
	public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		player.connection.send(payload);
	}

	@Override
	public void sendToServer(CustomPacketPayload payload) {
		Minecraft.getInstance().getConnection().send(payload);
	}

	@SuppressWarnings("unchecked")
	public void flush(PayloadRegistrar registrar) {
		for (PayloadEntry<?> s2cEntry : s2cEntries) {
			flushS2C(registrar, (PayloadEntry<CustomPacketPayload>) s2cEntry);
		}
		for (PayloadEntry<?> c2sEntry : c2sEntries) {
			boolean isAlsoS2C = s2cEntries.stream().anyMatch(e -> e.type().equals(c2sEntry.type()));
			if (!isAlsoS2C) {
				flushC2SOnly(registrar, (PayloadEntry<CustomPacketPayload>) c2sEntry);
			}
		}
		s2cEntries.clear();
		c2sEntries.clear();
		serverHandlers.clear();
		clientHandlers.clear();
	}

	private <P extends CustomPacketPayload> void flushS2C(PayloadRegistrar registrar, PayloadEntry<P> entry) {
		@Nullable IPayloadHandler<P> clientHandler = (IPayloadHandler<P>) clientHandlers.get(entry.type());
		boolean isAlsoC2S = c2sEntries.stream().anyMatch(e -> e.type().equals(entry.type()));

		if (isAlsoC2S) {
			@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) serverHandlers.get(entry.type());
			registrar.playBidirectional(entry.type(), entry.codec(), serverHandler != null ? serverHandler : (p, ctx) -> {}, clientHandler);
		} else {
			if (clientHandler != null) {
				registrar.playToClient(entry.type(), entry.codec(), clientHandler);
			} else {
				registrar.playToClient(entry.type(), entry.codec());
			}
		}
	}

	private <P extends CustomPacketPayload> void flushC2SOnly(PayloadRegistrar registrar, PayloadEntry<P> entry) {
		@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) serverHandlers.get(entry.type());
		if (serverHandler != null) {
			registrar.playToServer(entry.type(), entry.codec(), serverHandler);
		}
	}
}
