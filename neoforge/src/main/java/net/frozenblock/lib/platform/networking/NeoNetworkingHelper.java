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

package net.frozenblock.lib.platform.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenblock.lib.networking.ConfigPacketSender;
import net.frozenblock.lib.platform.service.NetworkingHelper;
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
import net.neoforged.neoforge.common.extensions.IServerConfigurationPacketListenerExtension;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public class NeoNetworkingHelper implements NetworkingHelper {
	private final List<PayloadEntry<?>> s2cEntries = new ArrayList<>();
	private final List<PayloadEntry<?>> c2sEntries = new ArrayList<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> serverHandlers = new HashMap<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> clientHandlers = new HashMap<>();

	private final List<ConfigPayloadEntry<?>> s2cConfigEntries = new ArrayList<>();
	private final List<ConfigPayloadEntry<?>> c2sConfigEntries = new ArrayList<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> serverConfigHandlers = new HashMap<>();
	private final Map<CustomPacketPayload.Type<?>, IPayloadHandler<?>> clientConfigHandlers = new HashMap<>();

	@Override
	public <P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		this.s2cEntries.add(new PayloadEntry<>(type, codec, false, 0));
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		this.c2sEntries.add(new PayloadEntry<>(type, codec, false, 0));
	}

	@Override
	public <P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		this.s2cEntries.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	@Override
	public <P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		this.c2sEntries.add(new PayloadEntry<>(type, codec, true, maxSize));
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		ServerPayloadHandler<P> handler
	) {
		this.serverHandlers.put(type, (payload, context) ->
			handler.receive((P) payload, ((ServerPlayer) context.player()).server, (ServerPlayer) context.player())
		);
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalClientReceiver(
		CustomPacketPayload.Type<P> type,
		ClientPayloadHandler<P> handler
	) {
		this.clientHandlers.put(type, (payload, context) ->
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

	@Override
	public <P extends CustomPacketPayload> void registerClientboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		this.s2cConfigEntries.add(new ConfigPayloadEntry<>(type, codec));
	}

	@Override
	public <P extends CustomPacketPayload> void registerServerboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		this.c2sConfigEntries.add(new ConfigPayloadEntry<>(type, codec));
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalServerConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ServerConfigPayloadHandler<P> handler
	) {
		this.serverConfigHandlers.put(type, (payload, context) -> {
			ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl) context.listener();
			handler.receive((P) payload, listener, wrapNeoSender(context.listener()));
		});
	}

	@Override
	public <P extends CustomPacketPayload> void registerGlobalClientConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ClientConfigPayloadHandler<P> handler
	) {
		this.clientConfigHandlers.put(type, (payload, context) ->
			handler.receive((P) payload, Minecraft.getInstance(), wrapNeoSender(context.listener()))
		);
	}

	@Override
	public boolean canSendConfigPacket(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> type) {
		return ((ICommonPacketListener) handler).hasChannel(type);
	}

	@Override
	public ConfigPacketSender getServerConfigSender(ServerConfigurationPacketListenerImpl handler) {
		return wrapNeoSender(handler);
	}

	@Override
	public void completeConfigTask(ServerConfigurationPacketListenerImpl handler, ConfigurationTask.Type type) {
		((IServerConfigurationPacketListenerExtension) handler).finishCurrentTask(type);
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
	public void flush(PayloadRegistrar registrar) {
		for (PayloadEntry<?> s2cEntry : this.s2cEntries) {
			flushS2C(registrar, (PayloadEntry<CustomPacketPayload>) s2cEntry);
		}
		for (PayloadEntry<?> c2sEntry : this.c2sEntries) {
			final boolean isAlsoS2C = this.s2cEntries.stream().anyMatch(e -> e.type().equals(c2sEntry.type()));
			if (!isAlsoS2C) flushC2SOnly(registrar, (PayloadEntry<CustomPacketPayload>) c2sEntry);
		}
		this.s2cEntries.clear();
		this.c2sEntries.clear();
		this.serverHandlers.clear();
		this.clientHandlers.clear();
	}

	@SuppressWarnings("unchecked")
	public void flushConfig(PayloadRegistrar registrar) {
		for (ConfigPayloadEntry<?> s2cEntry : this.s2cConfigEntries) {
			flushS2CConfig(registrar, (ConfigPayloadEntry<CustomPacketPayload>) s2cEntry);
		}
		for (ConfigPayloadEntry<?> c2sEntry : this.c2sConfigEntries) {
			final boolean isAlsoS2C = this.s2cConfigEntries.stream().anyMatch(e -> e.type().equals(c2sEntry.type()));
			if (!isAlsoS2C) flushC2SConfigOnly(registrar, (ConfigPayloadEntry<CustomPacketPayload>) c2sEntry);
		}
		this.s2cConfigEntries.clear();
		this.c2sConfigEntries.clear();
		this.serverConfigHandlers.clear();
		this.clientConfigHandlers.clear();
	}

	private <P extends CustomPacketPayload> void flushS2CConfig(PayloadRegistrar registrar, ConfigPayloadEntry<P> entry) {
		@Nullable IPayloadHandler<P> clientHandler = (IPayloadHandler<P>) this.clientConfigHandlers.get(entry.type());
		boolean isAlsoC2S = this.c2sConfigEntries.stream().anyMatch(e -> e.type().equals(entry.type()));

		if (isAlsoC2S) {
			@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) this.serverConfigHandlers.get(entry.type());
			registrar.configurationBidirectional(entry.type(), entry.codec(), serverHandler != null ? serverHandler : (p, ctx) -> {}, clientHandler);
		} else {
			if (clientHandler != null) {
				registrar.configurationToClient(entry.type(), entry.codec(), clientHandler);
			} else {
				registrar.configurationToClient(entry.type(), entry.codec());
			}
		}
	}

	private <P extends CustomPacketPayload> void flushC2SConfigOnly(PayloadRegistrar registrar, ConfigPayloadEntry<P> entry) {
		final @Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) this.serverConfigHandlers.get(entry.type());
		if (serverHandler != null) registrar.configurationToServer(entry.type(), entry.codec(), serverHandler);
	}

	private <P extends CustomPacketPayload> void flushS2C(PayloadRegistrar registrar, PayloadEntry<P> entry) {
		final @Nullable IPayloadHandler<P> clientHandler = (IPayloadHandler<P>) this.clientHandlers.get(entry.type());
		final boolean isAlsoC2S = this.c2sEntries.stream().anyMatch(e -> e.type().equals(entry.type()));

		if (isAlsoC2S) {
			@Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) this.serverHandlers.get(entry.type());
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
		final @Nullable IPayloadHandler<P> serverHandler = (IPayloadHandler<P>) this.serverHandlers.get(entry.type());
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
