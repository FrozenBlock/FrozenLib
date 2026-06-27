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

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.event.api.events.PlayerJoinEvents;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
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
import org.apache.commons.io.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// TODO PORT THESE TO NEOFORGE
public final class FrozenNetworkingFabric {

	public static void registerNetworking() {
		final var networking = FrozenLibInitPlatformUtils.NETWORKING;

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			CapeUtil.sendCapeReposToPlayer(player);
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

		networking.registerS2CPayloadType(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		networking.registerS2CPayloadType(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		networking.registerS2CPayloadType(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		networking.registerS2CPayloadType(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		networking.registerS2CPayloadType(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		networking.registerS2CPayloadType(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		networking.registerS2CPayloadType(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		networking.registerS2CPayloadType(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);
		networking.registerS2CPayloadType(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		networking.registerS2CPayloadType(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		networking.registerS2CPayloadType(CooldownTickCountPacket.PACKET_TYPE, CooldownTickCountPacket.CODEC);

		// CAPE
		networking.registerC2SPayloadType(CapeCustomizePacket.TYPE, CapeCustomizePacket.CODEC);
		networking.registerGlobalServerReceiver(CapeCustomizePacket.TYPE, (packet, server, player) -> CapeCustomizePacket.handle(packet, player));
		networking.registerS2CPayloadType(net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket.PACKET_TYPE, net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket.STREAM_CODEC);

		// DEBUG
		networking.registerS2CPayloadType(WindAccessPacket.TYPE, WindAccessPacket.STREAM_CODEC);
	}
}
