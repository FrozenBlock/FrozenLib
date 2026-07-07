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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.client.api.sounds.RelativeMovingSoundInstance;
import net.frozenblock.lib.sound.client.api.sounds.RestrictedMovingSound;
import net.frozenblock.lib.sound.client.api.sounds.RestrictedMovingSoundLoop;
import net.frozenblock.lib.sound.client.api.sounds.RestrictedStartingSound;
import net.frozenblock.lib.sound.client.api.sounds.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.client.api.sounds.distance_based.RestrictedMovingFadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.client.api.sounds.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.client.impl.FlyBySoundHub;
import net.frozenblock.lib.sound.impl.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.RelativeMovingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.texture.client.api.ServerTextureDownloader;
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class FrozenLibClientNetworking {

	@ApiStatus.Internal
	public static void registerClientReceivers() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, minecraft, player) ->
			ConfigEntrySyncPacket.receive(packet, null, null)
		);
		ClientConnectionEvents.DISCONNECT.register((handler, client) -> {
			for (ConfigEntry<?> config : ConfigV2Registry.allConfigEntries()) ConfigSyncModification.clearSyncData(config);
		});

		receiveLocalPlayerSoundPacket();
		receiveLocalSoundPacket();
		receiveRelativeMovingSoundPacket();
		receiveStartingMovingRestrictionSoundLoopPacket();
		receiveMovingRestrictionSoundPacket();
		receiveFadingDistanceSwitchingSoundPacket();
		receiveMovingFadingDistanceSwitchingSoundPacket();
		receiveFlyBySoundPacket();
		receiveCapeRepoPacket();
		receiveCooldownChangePacket();
		receiveForcedCooldownPacket();
		receiveCooldownTickCountPacket();
		receiveFileTransferPacket();
		receiveWindDebugPacket();
	}

	@ApiStatus.Internal
	private static void receiveWindDebugPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(WindAccessPacket.TYPE, (packet, minecraft, player) ->
			ClientWindUtil.Debug.addAccessedPosition(packet.accessPos())
		);
	}

	@ApiStatus.Internal
	private static void receiveLocalPlayerSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(LocalPlayerSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			minecraft.getSoundManager().play(
				new EntityBoundSoundInstance(
					packet.sound().value(),
					SoundSource.PLAYERS,
					packet.volume(),
					packet.pitch(),
					player,
					minecraft.level.getRandom().nextLong()
				)
			);
		});
	}

	@ApiStatus.Internal
	private static void receiveLocalSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(LocalSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final Vec3 pos = packet.pos();
			level.playLocalSound(pos.x, pos.y, pos.z, packet.sound().value(), packet.source(), packet.volume(), packet.pitch(), packet.distanceDelay());
		});
	}

	@ApiStatus.Internal
	private static void receiveRelativeMovingSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(RelativeMovingSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			if (player == null) return;
			minecraft.getSoundManager().play(
				new RelativeMovingSoundInstance(
					packet.sound().value(),
					packet.source(),
					packet.volume(),
					packet.pitch(),
					player,
					packet.pos(),
					minecraft.level.getRandom().nextLong()
				)
			);
		});
	}

	@ApiStatus.Internal
	private static <T extends Entity> void receiveStartingMovingRestrictionSoundLoopPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.id());
			if (entity == null) return;

			final SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			minecraft.getSoundManager().play(new RestrictedStartingSound<>(
				entity, packet.startingSound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(),
				new RestrictedMovingSoundLoop<>(
					entity, packet.sound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()
				)
			));
		});
	}

	@ApiStatus.Internal
	private static <T extends Entity> void receiveMovingRestrictionSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(MovingRestrictionSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.id());
			if (entity == null) return;

			final SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			if (packet.looping()) {
				minecraft.getSoundManager().play(new RestrictedMovingSoundLoop<>(entity, packet.sound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
			} else {
				minecraft.getSoundManager().play(new RestrictedMovingSound<>(entity, packet.sound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
			}
		});
	}

	private static void receiveFadingDistanceSwitchingSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			minecraft.getSoundManager().play(new FadingDistanceSwitchingSound(packet.closeSound().value(), packet.source(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), false, packet.pos()));
			minecraft.getSoundManager().play(new FadingDistanceSwitchingSound(packet.farSound().value(), packet.source(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), true, packet.pos()));
		});
	}

	@ApiStatus.Internal
	private static <T extends Entity> void receiveMovingFadingDistanceSwitchingSoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final SoundManager soundManager = minecraft.getSoundManager();
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.id());
			if (entity == null) return;

			final SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			if (packet.looping()) {
				soundManager.play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, packet.closeSound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), false));
				soundManager.play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, packet.farSound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), true));
			} else {
				soundManager.play(new RestrictedMovingFadingDistanceSwitchingSound<>(entity, packet.closeSound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), false));
				soundManager.play(new RestrictedMovingFadingDistanceSwitchingSound<>(entity, packet.farSound().value(), packet.source(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), true));
			}
		});
	}

	@ApiStatus.Internal
	private static void receiveFlyBySoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(FlyBySoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = (ClientLevel) player.level();
			final Entity entity = level.getEntity(packet.id());
			if (entity == null) return;

			final FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.source(), packet.sound().value());
			FlyBySoundHub.addEntity(entity, flyBySound);
		});
	}

	@ApiStatus.Internal
	private static void receiveCapeRepoPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(LoadCapeRepoPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			CapeUtil.registerCapesFromURL(packet.capeRepo());
		});
	}

	@ApiStatus.Internal
	private static void receiveFileTransferPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(FileTransferPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			if (!FrozenLibConfig.FILE_TRANSFER_CLIENT.get()) return;

			if (packet.request()) {
				final String requestPath = packet.transferPath();
				final String fileName = packet.fileName();
				final List<String> fileExtensions = packet.fileExtensions();
				if (!FileTransferFilter.isRequestAcceptable(requestPath, fileExtensions, null)) return;

				final Path requestedPath = minecraft.gameDirectory.toPath().resolve(requestPath);
				for (String fileExtension : fileExtensions) {
					final String fixedExtension = fileExtension.startsWith(".") ? fileExtension.substring(1) : fileExtension;
					final String fileNameWithExtension = fileName + "." + fixedExtension;
					final File file = requestedPath.resolve(fileNameWithExtension).toFile();
					final File localFile = requestedPath.resolve(FileTransferPacket.LOCAL_SOURCE).resolve(fileNameWithExtension).toFile();

					final File sendingFile = file.exists() ? file : localFile.exists() ? localFile : null;
					if (sendingFile == null) continue;

					if (FrozenLibNetworking.connectedToIntegratedServer()) {
						ServerTextureDownloader.registerTextureByPacketIfFound(packet.transferPath(), packet.fileName());
						return;
					} else {
						try {
							FrozenLibInitPlatformUtils.NETWORKING.sendToServer(FileTransferPacket.create(requestPath, sendingFile));
							return;
						} catch (IOException ignored) {}
					}
				}

				FrozenLibConstants.LOGGER.debug("Unable to create and send transfer packet for file {}!", packet.fileName());
			} else {
				final String destPath = packet.transferPath();
				final String fileName = packet.fileName();
				if (!FileTransferFilter.isTransferAcceptable(destPath, fileName, null)) return;

				try {
					final Path path = minecraft.gameDirectory.toPath().resolve(destPath).resolve(fileName);
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.data()), path.toFile());
					FrozenLibConstants.LOGGER.debug("Saved transferred file {} on client!", fileName);
					ServerTextureDownloader.registerTextureByPacketIfFound(packet.transferPath(), packet.fileName());
				} catch (IOException ignored) {
					FrozenLibConstants.LOGGER.error("Unable to save transferred file {} on client!", fileName);
				}
			}
		});
	}

	@ApiStatus.Internal
	private static void receiveCooldownChangePacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(CooldownChangePacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final Identifier cooldownGroup = packet.cooldownGroup();
			final int additional = packet.additional();
			player.getCooldowns().frozenLib$changeCooldown(cooldownGroup, additional);
		});
	}

	@ApiStatus.Internal
	private static void receiveForcedCooldownPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final Identifier cooldownGroup = packet.cooldownGroup();
			final int startTime = packet.startTime();
			final int endTime = packet.endTime();
			player.getCooldowns().cooldowns.put(cooldownGroup, new ItemCooldowns.CooldownInstance(startTime, endTime));
		});
	}

	@ApiStatus.Internal
	private static void receiveCooldownTickCountPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(CooldownTickCountPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			if (player != null) player.getCooldowns().tickCount = packet.count();
		});
	}

	public static boolean notConnected() {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return true;

		final LocalPlayer player = Minecraft.getInstance().player;
		return player == null;
	}

	public static boolean connectedToLan() {
		if (notConnected()) return false;
		final ServerData serverData = Minecraft.getInstance().getCurrentServer();
		if (serverData == null) return false;
		return serverData.isLan();
	}
}
