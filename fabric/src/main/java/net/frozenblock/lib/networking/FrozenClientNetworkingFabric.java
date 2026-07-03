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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
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

@Environment(EnvType.CLIENT)
public final class FrozenClientNetworkingFabric {

	public static void registerClientReceivers() {
		receiveLocalPlayerSoundPacket();
		receiveLocalSoundPacket();
		receiveRelativeMovingSoundPacket();
		receiveStartingMovingRestrictionSoundLoopPacket();
		receiveMovingRestrictionSoundPacket();
		receiveFadingDistanceSwitchingSoundPacket();
		receiveMovingFadingDistanceSwitchingSoundPacket();
		onReceiveFlyBySoundPacket();

		// DEBUG
		receiveWindDebugPacket();
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
	public static void onReceiveFlyBySoundPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(FlyBySoundPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			final ClientLevel level = (ClientLevel) player.level();
			final Entity entity = level.getEntity(packet.id());
			if (entity == null) return;

			final FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.source(), packet.sound().value());
			FlyBySoundHub.addEntity(entity, flyBySound);
		});
	}

	// DEBUG
	private static void receiveWindDebugPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(WindAccessPacket.TYPE, (packet, minecraft, player) -> {
			if (!FrozenLibConstants.DEBUG_WIND) return;
			ClientWindUtil.Debug.addAccessedPosition(packet.accessPos());
		});
	}
}
