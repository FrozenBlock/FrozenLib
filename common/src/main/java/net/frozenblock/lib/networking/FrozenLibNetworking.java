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

package net.frozenblock.lib.networking;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.event.api.events.PlayerJoinEvents;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.platform.FrozenLibEarlyPlatformUtils;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.sound.impl.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.RelativeMovingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;

public final class FrozenLibNetworking {

	@ApiStatus.Internal
	public static void registerNetworking() {
		final var networking = FrozenLibInitPlatformUtils.NETWORKING;

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigEntrySyncPacket.sendS2C(player);
		});

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			CapeUtil.sendCapeReposToPlayer(player);
		});

		networking.registerC2SPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		networking.registerS2CPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		networking.registerGlobalServerReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, server, player) -> {
			if (ConfigEntrySyncPacket.hasPermissionsToSendSync(player, true)) ConfigEntrySyncPacket.receive(packet, player, server);
		});

		networking.registerS2CLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		networking.registerC2SLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		networking.registerGlobalServerReceiver(FileTransferPacket.PACKET_TYPE, (packet, server, player) -> {
			if (packet.request()) {
				final String requestPath = packet.transferPath();
				final String fileName = packet.fileName();
				final List<String> fileExtensions = packet.fileExtensions();
				if (!FileTransferFilter.isRequestAcceptable(requestPath, fileExtensions, player)) return;

				final Path requestedPath = server.getServerDirectory().resolve(requestPath);
				for (String fileExtension : fileExtensions) {
					final String fixedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
					final String fileNameWithExtension = fileName + "." + fixedExtension;
					final File file = requestedPath.resolve(fileNameWithExtension).toFile();
					if (!file.exists()) continue;

					try {
						networking.sendToPlayer(player, FileTransferPacket.create(requestPath, file));
						return;
					} catch (IOException ignored) {}
				}

				FrozenLibConstants.LOGGER.debug("Unable to create and send transfer packets for file {} on server!", fileName);
			} else {
				if (!FrozenLibConfig.FILE_TRANSFER_SERVER.get()) return;

				final String destPath = packet.transferPath().replace("/" + FileTransferPacket.LOCAL_SOURCE, "");
				final String fileName = packet.fileName();
				if (!FileTransferFilter.isTransferAcceptable(destPath, fileName, player)) return;

				try {
					final Path path = server.getServerDirectory().resolve(destPath).resolve(packet.fileName());
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.data()), path.toFile());
					FrozenLibConstants.LOGGER.debug("Saved transferred file {} on server!", fileName);
				} catch (IOException ignored) {
					FrozenLibConstants.LOGGER.error("Unable to save transferred file {} on server!", packet.fileName());
				}
			}
		});

		networking.registerS2CPayloadType(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		networking.registerS2CPayloadType(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		networking.registerS2CPayloadType(CooldownTickCountPacket.PACKET_TYPE, CooldownTickCountPacket.CODEC);

		networking.registerS2CPayloadType(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		networking.registerS2CPayloadType(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		networking.registerS2CPayloadType(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		networking.registerS2CPayloadType(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		networking.registerS2CPayloadType(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		networking.registerS2CPayloadType(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		networking.registerS2CPayloadType(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		networking.registerS2CPayloadType(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);

		// CAPE
		networking.registerC2SPayloadType(CapeCustomizePacket.TYPE, CapeCustomizePacket.CODEC);
		networking.registerGlobalServerReceiver(CapeCustomizePacket.TYPE, (packet, server, player) -> CapeCustomizePacket.handle(packet, player));
		networking.registerS2CPayloadType(LoadCapeRepoPacket.PACKET_TYPE, LoadCapeRepoPacket.STREAM_CODEC);

		// WIND
		networking.registerS2CPayloadType(WindAccessPacket.TYPE, WindAccessPacket.STREAM_CODEC);
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
