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
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.event.api.events.client.ClientConnectionEvents;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.item.impl.cooldown.CooldownChangePacket;
import net.frozenblock.lib.item.impl.cooldown.ForcedCooldownPacket;
import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldowns;
import net.frozenblock.lib.item.impl.cooldown.SerializableItemCooldownsSyncPacket;
import net.frozenblock.lib.networking.api.ClientNetworkingHelper;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.lib.resource.client.api.texture.ServerTextureDownloader;
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
import net.frozenblock.lib.wind.client.ClientWindUtil;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;

@ClientOnly
@ApiStatus.Internal
public final class FrozenLibClientNetworking {

	public static void registerClientReceivers() {
		ClientNetworkingHelper.registerGlobalClientReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, minecraft, player) ->
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
		receiveSerializableItemCooldownsSyncPacket();
		receiveFileTransferPacket();
		receiveWindDebugPacket();
	}

	private static void receiveWindDebugPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(WindAccessPacket.TYPE, (packet, minecraft, player) ->
			ClientWindUtil.Debug.addAccessedPosition(packet.accessPos())
		);
	}

	private static void receiveLocalPlayerSoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(LocalPlayerSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
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

	private static void receiveLocalSoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(LocalSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final Vec3 pos = packet.pos();
			level.playLocalSound(pos.x, pos.y, pos.z, packet.sound().value(), packet.source(), packet.volume(), packet.pitch(), packet.distanceDelay());
		});
	}

	private static void receiveRelativeMovingSoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(RelativeMovingSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
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

	private static <T extends Entity> void receiveStartingMovingRestrictionSoundLoopPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.entityId());
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

	private static <T extends Entity> void receiveMovingRestrictionSoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(MovingRestrictionSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.entityId());
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
		ClientNetworkingHelper.registerGlobalClientReceiver(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			minecraft.getSoundManager().play(new FadingDistanceSwitchingSound(packet.closeSound().value(), packet.source(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), false, packet.pos()));
			minecraft.getSoundManager().play(new FadingDistanceSwitchingSound(packet.farSound().value(), packet.source(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), true, packet.pos()));
		});
	}

	private static <T extends Entity> void receiveMovingFadingDistanceSwitchingSoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final SoundManager soundManager = minecraft.getSoundManager();
			final ClientLevel level = minecraft.level;
			final T entity = (T) level.getEntity(packet.entityId());
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

	private static void receiveFlyBySoundPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(FlyBySoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = (ClientLevel) player.level();
			final Entity entity = level.getEntity(packet.entityId());
			if (entity == null) return;

			final FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.source(), packet.sound().value());
			FlyBySoundHub.addEntity(entity, flyBySound);
		});
	}

	private static void receiveCapeRepoPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(LoadCapeRepoPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			CapeUtil.registerCapesFromURL(packet.capeRepo());
		});
	}

	private static void receiveFileTransferPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(FileTransferPacket.PACKET_TYPE, (packet, minecraft, player) -> {
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

					if (NetworkingHelper.connectedToIntegratedServer()) {
						ServerTextureDownloader.registerTextureByPacketIfFound(packet.transferPath(), packet.fileName());
						return;
					} else {
						try {
							ClientNetworkingHelper.sendToServer(FileTransferPacket.create(requestPath, sendingFile));
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

	private static void receiveCooldownChangePacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(CooldownChangePacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final Identifier cooldownGroup = packet.cooldownGroup();
			final int additional = packet.additional();
			player.getCooldowns().frozenLib$changeCooldown(cooldownGroup, additional);
		});
	}

	private static void receiveForcedCooldownPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final Identifier cooldownGroup = packet.cooldownGroup();
			final int startTime = packet.startTime();
			final int endTime = packet.endTime();
			player.getCooldowns().cooldowns.put(cooldownGroup, new ItemCooldowns.CooldownInstance(startTime, endTime));
		});
	}

	private static void receiveSerializableItemCooldownsSyncPacket() {
		ClientNetworkingHelper.registerGlobalClientReceiver(SerializableItemCooldownsSyncPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			if (player == null) return;

			final int tickCount = packet.tickCount();
			final ItemCooldowns itemCooldowns = player.getCooldowns();
			itemCooldowns.tickCount = tickCount;

			for (SerializableItemCooldowns.ItemCooldown cooldown : packet.serializableItemCooldowns().cooldowns()) {
				final int cooldownLeft = cooldown.remainingTime();
				final int startTime = tickCount - (cooldown.totalTime() - cooldownLeft);
				final int endTime = tickCount + cooldownLeft;
				itemCooldowns.cooldowns.put(cooldown.group(), new ItemCooldowns.CooldownInstance(startTime, endTime));
			}
		});
	}
}
