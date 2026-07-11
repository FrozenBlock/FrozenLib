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

package net.frozenblock.lib.networking.api.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.lib.networking.impl.ConfigPacketSender;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public final class NetworkingHelperImpl {
	private static final List<PayloadEntry<?>> S2C_ENTRIES = new ArrayList<>();
	private static final List<PayloadEntry<?>> C2S_ENTRIES = new ArrayList<>();
	private static final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> SERVER_HANDLERS = new HashMap<>();
	private static final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> CLIENT_HANDLERS = new HashMap<>();

	private static final List<ConfigPayloadEntry<?>> S2C_CONFIG_ENTRIES = new ArrayList<>();
	private static final List<ConfigPayloadEntry<?>> C2S_CONFIG_ENTRIES = new ArrayList<>();
	private static final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> SERVER_CONFIG_HANDLERS = new HashMap<>();
	private static final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> CLIENT_CONFIG_HANDLERS = new HashMap<>();

	public static <P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		S2C_ENTRIES.add(new PayloadEntry<>(type, codec, false, 0));
	}

	public static <P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		C2S_ENTRIES.add(new PayloadEntry<>(type, codec, false, 0));
	}

	public static <P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		S2C_ENTRIES.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	public static <P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		C2S_ENTRIES.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	public static <P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		NetworkingHelper.ServerPayloadHandler<P> handler
	) {
		SERVER_HANDLERS.put(type, (payload, context) ->
			handler.receive((P) payload, ((ServerPlayer) context.player()).server, (ServerPlayer) context.player())
		);
	}

	@ClientOnly
	public static <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		NetworkingHelper.ClientPayloadHandler<P> handler
	) {
		// FIXME: referencing Minecraft and LocalPlayer crash servers, silly!
		CLIENT_HANDLERS.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), (LocalPlayer) context.player())
		);
	}

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		player.connection.send(payload);
	}

	@ClientOnly
	public static void sendToServer(CustomPacketPayload payload) {
		Minecraft.getInstance().getConnection().send(payload);
	}

	public static <P extends CustomPacketPayload> void registerClientboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		S2C_CONFIG_ENTRIES.add(new ConfigPayloadEntry<>(type, codec));
	}

	public static <P extends CustomPacketPayload> void registerServerboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		C2S_CONFIG_ENTRIES.add(new ConfigPayloadEntry<>(type, codec));
	}

	public static <P extends CustomPacketPayload> void registerGlobalServerConfigReceiver(
		CustomPacketPayload.Type<P> type,
		NetworkingHelper.ServerConfigPayloadHandler<P> handler
	) {
		SERVER_CONFIG_HANDLERS.put(type, (payload, context) -> {
			ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl) context.listener();
			handler.receive((P) payload, listener, wrapNeoSender(context.listener()));
		});
	}

	@ClientOnly
	public static <P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		NetworkingHelper.ClientConfigPayloadHandler<P> handler
	) {
		CLIENT_CONFIG_HANDLERS.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), wrapNeoSender(context.listener()))
		);
	}

	public static boolean canSendConfigPacket(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> type) {
		return handler.hasChannel(type);
	}

	public static ConfigPacketSender getServerConfigSender(ServerConfigurationPacketListenerImpl handler) {
		return wrapNeoSender(handler);
	}

	public static void completeConfigTask(ServerConfigurationPacketListenerImpl handler, ConfigurationTask.Type type) {
		handler.finishCurrentTask(type);
	}

	private static ConfigPacketSender wrapNeoSender(Object listener) {
		ICommonPacketListener neo = (ICommonPacketListener) listener;
		return new ConfigPacketSender() {
			@Override
			public void sendPacket(CustomPacketPayload payload) {
				neo.send(payload);
			}

			@Override
			public void disconnect(Component reason) {
				neo.disconnect(reason);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static void flush(PayloadRegistrar registrar) {
		for (PayloadEntry<?> s2cEntry : S2C_ENTRIES) {
			flushS2C(registrar, (PayloadEntry<CustomPacketPayload>) s2cEntry);
		}
		for (PayloadEntry<?> c2sEntry : C2S_ENTRIES) {
			final boolean isAlsoS2C = S2C_ENTRIES.stream().anyMatch(e -> e.type().equals(c2sEntry.type()));
			if (!isAlsoS2C) flushC2SOnly(registrar, (PayloadEntry<CustomPacketPayload>) c2sEntry);
		}
		S2C_ENTRIES.clear();
		C2S_ENTRIES.clear();
		SERVER_HANDLERS.clear();
		CLIENT_HANDLERS.clear();
	}

	@SuppressWarnings("unchecked")
	public static void flushConfig(PayloadRegistrar registrar) {
		for (ConfigPayloadEntry<?> s2cEntry : S2C_CONFIG_ENTRIES) {
			flushS2CConfig(registrar, (ConfigPayloadEntry<CustomPacketPayload>) s2cEntry);
		}
		for (ConfigPayloadEntry<?> c2sEntry : C2S_CONFIG_ENTRIES) {
			final boolean isAlsoS2C = S2C_CONFIG_ENTRIES.stream().anyMatch(e -> e.type().equals(c2sEntry.type()));
			if (!isAlsoS2C) flushC2SConfigOnly(registrar, (ConfigPayloadEntry<CustomPacketPayload>) c2sEntry);
		}
		S2C_CONFIG_ENTRIES.clear();
		C2S_CONFIG_ENTRIES.clear();
		SERVER_CONFIG_HANDLERS.clear();
		CLIENT_CONFIG_HANDLERS.clear();
	}

	private static <P extends CustomPacketPayload> void flushS2CConfig(PayloadRegistrar registrar, ConfigPayloadEntry<P> entry) {
		@Nullable IPayloadHandler<P> clientHandler = (IPayloadHandler<P>) CLIENT_CONFIG_HANDLERS.get(entry.type());
		boolean isAlsoC2S = C2S_CONFIG_ENTRIES.stream().anyMatch(e -> e.type().equals(entry.type()));

		if (isAlsoC2S) {
			@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) SERVER_CONFIG_HANDLERS.get(entry.type());
			registrar.configurationBidirectional(entry.type(), entry.codec(), serverHandler != null ? serverHandler : (p, ctx) -> {}, clientHandler);
		} else {
			if (clientHandler != null) {
				registrar.configurationToClient(entry.type(), entry.codec(), clientHandler);
			} else {
				registrar.configurationToClient(entry.type(), entry.codec());
			}
		}
	}

	private static <P extends CustomPacketPayload> void flushC2SConfigOnly(PayloadRegistrar registrar, ConfigPayloadEntry<P> entry) {
		final @Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) SERVER_CONFIG_HANDLERS.get(entry.type());
		if (serverHandler != null) registrar.configurationToServer(entry.type(), entry.codec(), serverHandler);
	}

	private static <P extends CustomPacketPayload> void flushS2C(PayloadRegistrar registrar, PayloadEntry<P> entry) {
		final @Nullable IPayloadHandler<P> clientHandler = (IPayloadHandler<P>) CLIENT_HANDLERS.get(entry.type());
		final boolean isAlsoC2S = C2S_ENTRIES.stream().anyMatch(e -> e.type().equals(entry.type()));

		if (isAlsoC2S) {
			@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) SERVER_HANDLERS.get(entry.type());
			registrar.playBidirectional(entry.type(), entry.codec(), serverHandler != null ? serverHandler : (p, ctx) -> {}, clientHandler);
		} else {
			if (clientHandler != null) {
				registrar.playToClient(entry.type(), entry.codec(), clientHandler);
			} else {
				registrar.playToClient(entry.type(), entry.codec());
			}
		}
	}

	private static <P extends CustomPacketPayload> void flushC2SOnly(PayloadRegistrar registrar, PayloadEntry<P> entry) {
		final @Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) SERVER_HANDLERS.get(entry.type());
		if (serverHandler != null) registrar.playToServer(entry.type(), entry.codec(), serverHandler);
	}

	private record PayloadEntry<P extends CustomPacketPayload>(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		boolean large,
		int maxSize
	) {}

	private record ConfigPayloadEntry<P extends CustomPacketPayload>(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {}
}
