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
import java.util.concurrent.CompletableFuture;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.event.api.events.ServerPlayerEvents;
import net.frozenblock.lib.file.transfer.FileTransferEvents;
import net.frozenblock.lib.file.transfer.FileTransferFailPacket;
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
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FrozenLibNetworking {

	public static void registerNetworking() {
		ServerPlayerEvents.JOIN.register((server, player) -> {
			ConfigEntrySyncPacket.sendS2C(player);
		});

		ServerPlayerEvents.JOIN.register((server, player) -> {
			CapeUtil.sendCapeReposToPlayer(player);
			// FileTransferPacket.sendToPlayer(new File("server/logs/latest.log"), "server/logs", player);
		});

		NetworkingHelper.registerC2SPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(ConfigEntrySyncPacket.PACKET_TYPE, ConfigEntrySyncPacket.CODEC);
		NetworkingHelper.registerGlobalServerReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, server, player) -> {
			if (ConfigEntrySyncPacket.hasPermissionsToSendSync(player, true)) ConfigEntrySyncPacket.receive(packet, player, server);
		});

		NetworkingHelper.registerS2CLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		NetworkingHelper.registerC2SLargePayloadType(FileTransferPacket.PACKET_TYPE, FileTransferPacket.CODEC, FileTransferPacket.MAX_SIZE_PER_TRANSFER);
		receiveFileTransferPacket();

		NetworkingHelper.registerS2CPayloadType(FileTransferFailPacket.PACKET_TYPE, FileTransferFailPacket.CODEC);
		NetworkingHelper.registerC2SPayloadType(FileTransferFailPacket.PACKET_TYPE, FileTransferFailPacket.CODEC);
		receiveFileTransferFailPacket();

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
		NetworkingHelper.registerS2CPayloadType(LoadCapeRepoPacket.PACKET_TYPE, LoadCapeRepoPacket.CODEC);

		// WIND
		NetworkingHelper.registerS2CPayloadType(WindAccessPacket.TYPE, WindAccessPacket.CODEC);
	}

	private static void receiveFileTransferPacket() {
		NetworkingHelper.registerGlobalServerReceiver(FileTransferPacket.PACKET_TYPE, (packet, server, player) -> {
			if (packet.request()) { // Sending
				final String requestPath = packet.transferPath();
				final String fileName = packet.fileName();
				final List<String> fileExtensions = packet.fileExtensions();

				if (!FileTransferFilter.isRequestAcceptable(requestPath, fileExtensions, player)) {
					FileTransferEvents.ILLEGAL_REQUEST_RECEIVE.invoker().onIllegalRequestReceived(requestPath, fileName, fileExtensions, false);
					return;
				}

				FileTransferEvents.REQUEST_RECEIVE.invoker().onRequestReceived(requestPath, fileName, fileExtensions, false);

				final Path defaultPath = server.getServerDirectory().resolve(requestPath);
				final Path localPath = server.getServerDirectory().resolve(requestPath).resolve(FileTransferPacket.LOCAL_SOURCE);
				for (Path requestedPath : new Path[]{defaultPath, localPath}) {
					for (String fileExtension : fileExtensions) {
						final String fixedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
						final String fileNameWithExtension = fileName + "." + fixedExtension;
						final File file = requestedPath.resolve(fileNameWithExtension).toFile();
						if (!file.exists()) continue;

						try {
							NetworkingHelper.sendToPlayer(player, FileTransferPacket.create(requestPath, file));
							FileTransferEvents.FILE_SEND.invoker().onFileSent(requestPath, fileName, file, false);
							return;
						} catch (IOException ignored) {}
					}
				}

				FileTransferEvents.TRANSFER_FAIL.invoker().onTransferFailed(requestPath, fileName, false, false);
				FileTransferFailPacket.sendToPlayer(fileName, requestPath, true, player);
				FrozenLibConstants.LOGGER.debug("Unable to create and send transfer packet for file {} on server!", fileName);
			} else { // Receiving
				final String destinationPath = packet.transferPath().replace("/" + FileTransferPacket.LOCAL_SOURCE, "");
				final String fileName = packet.fileName();
				final String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);

				if (!FrozenLibConfig.FILE_TRANSFER_SERVER.get()) {
					NetworkingHelper.sendToPlayer(player, FileTransferFailPacket.create(destinationPath, fileNameWithoutExtension, false));
					FileTransferEvents.REQUEST_FAIL.invoker().onRequestFailed(destinationPath, fileNameWithoutExtension, true, false);
					return;
				}

				if (!FileTransferFilter.isTransferAcceptable(destinationPath, fileName, player)) {
					FileTransferEvents.ILLEGAL_TRANSFER_RECEIVE.invoker().onIllegalTransferReceived(destinationPath, fileNameWithoutExtension, false);
					return;
				}

				final Path filePath = server.getServerDirectory().resolve(destinationPath).resolve(packet.fileName());
				CompletableFuture.runAsync(() -> {
					try {
						FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.data()), filePath.toFile());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}).whenComplete((ignored, throwable) -> {
					if (throwable != null) {
						FileTransferEvents.REQUEST_FAIL.invoker().onRequestFailed(destinationPath, fileNameWithoutExtension, false, false);
						FrozenLibConstants.LOGGER.error("Unable to save transferred file {} on server!", fileName);
					} else {
						FileTransferEvents.FILE_RECEIVE.invoker().onFileReceived(destinationPath, fileNameWithoutExtension, filePath.toFile(), false);
						FrozenLibConstants.LOGGER.debug("Saved transferred file {} on server!", fileName);
					}
				});
			}
		});
	}

	private static void receiveFileTransferFailPacket() {
		NetworkingHelper.registerGlobalServerReceiver(FileTransferFailPacket.PACKET_TYPE, (packet, server, player) -> {
			if (packet.request()) {
				FileTransferEvents.REQUEST_FAIL.invoker().onRequestFailed(packet.targetPath(), packet.fileName(), false, false);
			} else {
				FileTransferEvents.TRANSFER_FAIL.invoker().onTransferFailed(packet.targetPath(), packet.fileName(), false, false);
			}
		});
	}
}
