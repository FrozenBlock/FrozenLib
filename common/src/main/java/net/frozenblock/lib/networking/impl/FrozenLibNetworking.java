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

package net.frozenblock.lib.networking.impl;

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
import net.frozenblock.lib.item.impl.cooldown.CooldownChangePacket;
import net.frozenblock.lib.item.impl.cooldown.ForcedCooldownPacket;
import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldownsSyncPacket;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.lib.sound.impl.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.RelativeMovingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibNetworking {

	public static void registerNetworking() {
		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigEntrySyncPacket.sendS2C(player);
		});

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			CapeUtil.sendCapeReposToPlayer(player);
		});

		NetworkingHelper.registerC2SPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		NetworkingHelper.registerGlobalServerReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, server, player) -> {
			if (ConfigEntrySyncPacket.hasPermissionsToSendSync(player, true)) ConfigEntrySyncPacket.receive(packet, player, server);
		});

		NetworkingHelper.registerS2CLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		NetworkingHelper.registerC2SLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		NetworkingHelper.registerGlobalServerReceiver(FileTransferPacket.PACKET_TYPE, (packet, server, player) -> {
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
						NetworkingHelper.sendToPlayer(player, FileTransferPacket.create(requestPath, file));
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

		NetworkingHelper.registerS2CPayloadType(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(SerializableItemCooldownsSyncPacket.PACKET_TYPE, SerializableItemCooldownsSyncPacket.CODEC);

		NetworkingHelper.registerS2CPayloadType(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);

		// CAPE
		NetworkingHelper.registerC2SPayloadType(CapeCustomizePacket.TYPE, CapeCustomizePacket.CODEC);
		NetworkingHelper.registerGlobalServerReceiver(CapeCustomizePacket.TYPE, (packet, server, player) -> CapeCustomizePacket.handle(packet, player));
		NetworkingHelper.registerS2CPayloadType(LoadCapeRepoPacket.PACKET_TYPE, LoadCapeRepoPacket.STREAM_CODEC);

		// WIND
		NetworkingHelper.registerS2CPayloadType(WindAccessPacket.TYPE, WindAccessPacket.STREAM_CODEC);
	}
}
