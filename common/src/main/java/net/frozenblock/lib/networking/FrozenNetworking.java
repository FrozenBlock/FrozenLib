package net.frozenblock.lib.networking;

import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.event.api.events.PlayerJoinEvents;
import net.frozenblock.lib.platform.FrozenLibEarlyPlatformUtils;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class FrozenNetworking {

	public static void registerNetworking() {
		final var networking = FrozenLibInitPlatformUtils.NETWORKING;

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigEntrySyncPacket.sendS2C(player);
		});

		networking.registerC2SPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		networking.registerS2CPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		networking.registerGlobalServerReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, server, player) -> {
			if (ConfigEntrySyncPacket.hasPermissionsToSendSync(player, true)) ConfigEntrySyncPacket.receive(packet, player, server);
		});
	}

	public static void sendPacketToAllPlayers(ServerLevel level, CustomPacketPayload payload) {
		final Packet<?> packet = new ClientboundCustomPayloadPacket(payload);
		for (ServerPlayer serverPlayer : level.players()) serverPlayer.connection.send(packet);
	}

	public static boolean isLocalPlayer(Player player) {
		if (FrozenLibEarlyPlatformUtils.LOADER.isServer()) return false;
		return Minecraft.getInstance().isLocalPlayer(player.getGameProfile().id());
	}

	public static boolean connectedToIntegratedServer() {
		if (FrozenLibEarlyPlatformUtils.LOADER.isServer()) return false;
		final Minecraft minecraft = Minecraft.getInstance();
		return minecraft.hasSingleplayerServer();
	}

	/**
	 * @return if the client is connected to any server
	 */
	public static boolean connectedToServer() {
		if (FrozenLibEarlyPlatformUtils.LOADER.isServer()) return false;

		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return false;

		return listener.getConnection().isConnected();
	}

	/**
	 * @return if the current server is multiplayer (LAN/dedicated) or not (singleplayer)
	 */
	public static boolean isMultiplayer() {
		if (FrozenLibEarlyPlatformUtils.LOADER.isServer()) return true;
		return !Minecraft.getInstance().hasSingleplayerServer();
	}

}
