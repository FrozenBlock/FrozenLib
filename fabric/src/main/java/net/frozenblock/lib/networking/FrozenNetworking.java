/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.frozenblock.lib.platform.FrozenEarlyPlatformUtils;
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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;

public final class FrozenNetworking {

	public static void registerNetworking() {
		final PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.clientboundPlay();
		final PayloadTypeRegistry<RegistryFriendlyByteBuf> c2sRegistry = PayloadTypeRegistry.serverboundPlay();

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigEntrySyncPacket.sendS2C(player);
			CapeUtil.sendCapeReposToPlayer(player);
		});

		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, error) -> {
			if (error != null || server == null) return;
			for (ServerPlayer player : PlayerLookup.all(server)) {
				ConfigEntrySyncPacket.sendS2C(player);
			}
		});

		c2sRegistry.register(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		registry.register(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ConfigEntrySyncPacket.PACKET_TYPE, ((packet, ctx) -> {
			if (ConfigEntrySyncPacket.hasPermissionsToSendSync(ctx.player(), true)) ConfigEntrySyncPacket.receive(packet, ctx.player(), ctx.server());
		}));

		registry.register(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		registry.register(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		registry.register(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		registry.register(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		registry.register(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		registry.register(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		registry.register(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		registry.register(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);
		registry.register(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		registry.register(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		registry.register(CooldownTickCountPacket.PACKET_TYPE, CooldownTickCountPacket.CODEC);

		// CAPE
		c2sRegistry.register(CapeCustomizePacket.TYPE, CapeCustomizePacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CapeCustomizePacket.TYPE, CapeCustomizePacket::handle);
		registry.register(LoadCapeRepoPacket.PACKET_TYPE, LoadCapeRepoPacket.STREAM_CODEC);

		// FILE TRANSFER
		registry.registerLarge(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		c2sRegistry.registerLarge(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		ServerPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE, (packet, ctx) -> {
			if (packet.request()) {
				final String requestPath = packet.transferPath();
				final String fileName = packet.fileName();
				final List<String> fileExtensions = packet.fileExtensions();
				if (!FileTransferFilter.isRequestAcceptable(requestPath, fileExtensions, ctx.player())) return;

				final Path requestedPath = ctx.server().getServerDirectory().resolve(requestPath);
				for (String fileExtension : fileExtensions) {
					final String fixedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
					final String fileNameWithExtension = fileName + "." + fixedExtension;
					final File file = requestedPath.resolve(fileNameWithExtension).toFile();
					if (!file.exists()) continue;

					try {
						ServerPlayNetworking.send(ctx.player(), FileTransferPacket.create(requestPath, file));
						return;
					} catch (IOException ignored) {}
				}

				FrozenLibConstants.LOGGER.debug("Unable to create and send transfer packets for file {} on server!", fileName);
			} else {
				if (!FrozenLibConfig.FILE_TRANSFER_SERVER.get()) return;

				final String destPath = packet.transferPath().replace("/" + FileTransferPacket.LOCAL_SOURCE, "");
				final String fileName = packet.fileName();
				if (!FileTransferFilter.isTransferAcceptable(destPath, fileName, ctx.player())) return;

				try {
					final Path path = ctx.server().getServerDirectory().resolve(destPath).resolve(packet.fileName());
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.data()), path.toFile());
					FrozenLibConstants.LOGGER.debug("Saved transferred file {} on server!", fileName);
				} catch (IOException ignored) {
					FrozenLibConstants.LOGGER.error("Unable to save transferred file {} on server!", packet.fileName());
				}
			}
		});

		// DEBUG
		registry.register(WindAccessPacket.TYPE, WindAccessPacket.STREAM_CODEC);
	}

	public static void sendPacketToAllPlayers(ServerLevel level, CustomPacketPayload payload) {
		final Packet<?> packet = new ClientboundCustomPayloadPacket(payload);
		for (ServerPlayer serverPlayer : level.players()) serverPlayer.connection.send(packet);
	}

	public static boolean isLocalPlayer(Player player) {
		if (FrozenEarlyPlatformUtils.LOADER.isServer()) return false;
		return Minecraft.getInstance().isLocalPlayer(player.getGameProfile().id());
	}

	public static boolean connectedToIntegratedServer() {
		if (FrozenEarlyPlatformUtils.LOADER.isServer()) return false;
		final Minecraft minecraft = Minecraft.getInstance();
		return minecraft.hasSingleplayerServer();
	}

	/**
	 * @return if the client is connected to any server
	 */
	public static boolean connectedToServer() {
		if (FrozenEarlyPlatformUtils.LOADER.isServer()) return false;

		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return false;

		return listener.getConnection().isConnected();
	}

	/**
	 * @return if the current server is multiplayer (LAN/dedicated) or not (singleplayer)
	 */
	public static boolean isMultiplayer() {
		if (FrozenEarlyPlatformUtils.LOADER.isServer()) return true;
		return !Minecraft.getInstance().hasSingleplayerServer();
	}

}
