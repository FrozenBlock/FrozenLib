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

package net.frozenblock.lib.networking.api;

import net.frozenblock.lib.networking.impl.ConfigPacketSender;
import net.frozenblock.lib.platform.ModLoader;
import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.entity.player.Player;

public final class NetworkingHelper {

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerS2CPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerC2SPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerS2CLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerC2SLargePayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<RegistryFriendlyByteBuf, P> codec,
		int maxSize
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerGlobalServerReceiver(
		CustomPacketPayload.Type<P> type,
		ServerPayloadHandler<P> handler
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerClientboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerServerboundConfigPayloadType(
		CustomPacketPayload.Type<P> type,
		StreamCodec<FriendlyByteBuf, P> codec
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static <P extends CustomPacketPayload> void registerGlobalServerConfigReceiver(
		CustomPacketPayload.Type<P> type,
		ServerConfigPayloadHandler<P> handler
	) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean canSendConfigPacket(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> type) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static ConfigPacketSender getServerConfigSender(ServerConfigurationPacketListenerImpl handler) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void completeConfigTask(ServerConfigurationPacketListenerImpl handler, ConfigurationTask.Type type) {
		throw new AssertionError();
	}

	public static void sendPacketToAllPlayers(ServerLevel level, CustomPacketPayload payload) {
		final Packet<?> packet = new ClientboundCustomPayloadPacket(payload);
		for (ServerPlayer serverPlayer : level.players()) serverPlayer.connection.send(packet);
	}

	public static boolean isLocalPlayer(Player player) {
		if (ModLoader.isServer()) return false;
		return Minecraft.getInstance().isLocalPlayer(player.getGameProfile().id());
	}

	public static boolean connectedToIntegratedServer() {
		if (ModLoader.isServer()) return false;
		final Minecraft minecraft = Minecraft.getInstance();
		return minecraft.hasSingleplayerServer();
	}

	/**
	 * @return if the client is connected to any server
	 */
	public static boolean connectedToServer() {
		if (ModLoader.isServer()) return false;

		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return false;

		return listener.getConnection().isConnected();
	}

	/**
	 * @return if the current server is multiplayer (LAN/dedicated) or not (singleplayer)
	 */
	public static boolean isMultiplayer() {
		if (ModLoader.isServer()) return true;
		return !Minecraft.getInstance().hasSingleplayerServer();
	}

	@FunctionalInterface
	public interface ServerPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, MinecraftServer server, ServerPlayer player);
	}

	@FunctionalInterface
	public interface ServerConfigPayloadHandler<P extends CustomPacketPayload> {
		void receive(P payload, ServerConfigurationPacketListenerImpl listener, ConfigPacketSender sender);
	}
}
