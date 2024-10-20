/*
 * Copyright (C) 2024 FrozenBlock
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.client.impl.ClientCapeData;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.image_transfer.FileTransferPacket;
import net.frozenblock.lib.image_transfer.client.ServerTexture;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.screenshake.impl.network.EntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveEntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.ScreenShakePacket;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSound;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSoundLoop;
import net.frozenblock.lib.sound.api.instances.RestrictedStartingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.api.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.api.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconRemovePacket;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.frozenblock.lib.wind.impl.networking.WindSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FileUtils;

@Environment(EnvType.CLIENT)
public final class FrozenClientNetworking {

	private static PayloadTypeRegistry<RegistryFriendlyByteBuf> registry() {
		return PayloadTypeRegistry.playS2C();
	}

	public static void registerClientReceivers() {
		receiveLocalPlayerSoundPacket();
		receiveLocalSoundPacket();
		receiveStartingMovingRestrictionSoundLoopPacket();
		receiveMovingRestrictionSoundPacket();
		receiveFadingDistanceSwitchingSoundPacket();
		receiveMovingFadingDistanceSwitchingSoundPacket();
		registry().register(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket::receive);
		receiveCooldownChangePacket();
		receiveForcedCooldownPacket();
		receiveCooldownTickCountPacket();
		receiveScreenShakePacket();
		receiveScreenShakeFromEntityPacket();
		receiveRemoveScreenShakePacket();
		receiveRemoveScreenShakeFromEntityPacket();
		receiveIconPacket();
		receiveIconRemovePacket();
		receiveWindSyncPacket();
		receiveWindDisturbancePacket();
		receiveTransferImagePacket();
		receiveCapePacket();
		receiveCapeRepoPacket();
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, (packet, ctx) ->
			ConfigSyncPacket.receive(packet, null)
		);
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			for (Config<?> config : ConfigRegistry.getAllConfigs()) {
				ConfigRegistry.setSyncData(config, null);
				config.setSynced(false);
			}
		}));

		ClientPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE, (packet, ctx) -> {
			if (!FrozenLibConfig.FILE_TRANSFER_CLIENT) return;
			if (packet.request()) {
				Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
				try {
					FileTransferPacket fileTransferPacket = FileTransferPacket.create(packet.transferPath(), path.toFile());
					ClientPlayNetworking.send(fileTransferPacket);
				} catch (IOException ignored) {
				}
			} else {
				try {
					Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.bytes()), path.toFile());
					ServerTexture serverTexture = ServerTexture.WAITING_TEXTURES.get(packet.transferPath() + "/" + packet.fileName());
					if (serverTexture != null) {
						serverTexture.runFutureForTexture();
					}
				} catch (IOException ignored) {
				}
			}
		});
	}

	private static void receiveLocalPlayerSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(LocalPlayerSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = ctx.player();
			Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(packet.sound().value(), SoundSource.PLAYERS, packet.volume(), packet.pitch(), player, ctx.client().level.random.nextLong()));
		});
	}

	private static void receiveLocalSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(LocalSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientLevel level = ctx.client().level;
			Vec3 pos = packet.pos();
			level.playLocalSound(pos.x, pos.y, pos.z, packet.sound().value(), packet.category(), packet.volume(), packet.pitch(), packet.distanceDelay());
		});
	}

	private static <T extends Entity> void receiveStartingMovingRestrictionSoundLoopPacket() {
		ClientPlayNetworking.registerGlobalReceiver(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientLevel level = ctx.client().level;
			T entity = (T) level.getEntity(packet.id());
			if (entity != null) {
				SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
				Minecraft.getInstance().getSoundManager().play(new RestrictedStartingSound<>(
					entity, packet.startingSound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(),
					new RestrictedMovingSoundLoop<>(
						entity, packet.sound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()
					)
				));
			}
		});
	}

	private static <T extends Entity> void receiveMovingRestrictionSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(MovingRestrictionSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientLevel level = ctx.client().level;
			T entity = (T) level.getEntity(packet.id());
			if (entity != null) {
				SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
				if (packet.looping())
					Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSoundLoop<>(entity, packet.sound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
				else
					Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSound<>(entity, packet.sound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
			}
		});
	}

	private static void receiveFadingDistanceSwitchingSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			ctx.client().getSoundManager().play(new FadingDistanceSwitchingSound(packet.closeSound().value(), packet.category(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), false, packet.pos()));
			ctx.client().getSoundManager().play(new FadingDistanceSwitchingSound(packet.farSound().value(), packet.category(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), true, packet.pos()));
		});
	}

	private static <T extends Entity> void receiveMovingFadingDistanceSwitchingSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			SoundManager soundManager = ctx.client().getSoundManager();
			ClientLevel level = ctx.client().level;
			T entity = (T) level.getEntity(packet.id());
			if (entity != null) {
				SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
				if (packet.looping()) {
					soundManager.play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, packet.closeSound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), false));
					soundManager.play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, packet.farSound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), true));
				} else {
					soundManager.play(new RestrictedMovingFadingDistanceSwitchingSound<>(entity, packet.closeSound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), false));
					soundManager.play(new RestrictedMovingFadingDistanceSwitchingSound<>(entity, packet.farSound().value(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath(), packet.fadeDist(), packet.maxDist(), packet.volume(), true));
				}
			}
		});
	}

	private static void receiveCooldownChangePacket() {
		ClientPlayNetworking.registerGlobalReceiver(CooldownChangePacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = ctx.player();
			ResourceLocation cooldownGroup = packet.cooldownGroup();
			int additional = packet.additional();
			((CooldownInterface) player.getCooldowns()).frozenLib$changeCooldown(cooldownGroup, additional);
		});
	}

	private static void receiveForcedCooldownPacket() {
		ClientPlayNetworking.registerGlobalReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = ctx.player();
			ResourceLocation cooldownGroup = packet.cooldownGroup();
			int startTime = packet.startTime();
			int endTime = packet.endTime();
			player.getCooldowns().cooldowns.put(cooldownGroup, new ItemCooldowns.CooldownInstance(startTime, endTime));
		});
	}

	private static void receiveCooldownTickCountPacket() {
		ClientPlayNetworking.registerGlobalReceiver(CooldownTickCountPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = ctx.player();
			if (player != null) {
				player.getCooldowns().tickCount = packet.count();
			}
		});
	}

	private static void receiveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenShakePacket.PACKET_TYPE, (packet, ctx) -> {
			float intensity = packet.intensity();
			int duration = packet.duration();
			int fallOffStart = packet.falloffStart();
			Vec3 pos = packet.pos();
			float maxDistance = packet.maxDistance();
			int ticks = packet.ticks();

			ClientLevel level = ctx.client().level;
            ScreenShaker.addShake(level, intensity, duration, fallOffStart, pos, maxDistance, ticks);
        });
	}

	private static void receiveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(EntityScreenShakePacket.PACKET_TYPE, (packet, ctx) -> {
			int id = packet.entityId();
			float intensity = packet.intensity();
			int duration = packet.duration();
			int fallOffStart = packet.falloffStart();
			float maxDistance = packet.maxDistance();
			int ticks = packet.ticks();

			ClientLevel level = ctx.client().level;
            Entity entity = level.getEntity(id);
            if (entity != null) {
                ScreenShaker.addShake(entity, intensity, duration, fallOffStart, maxDistance, ticks);
            }
		});
	}

	private static void receiveRemoveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(RemoveScreenShakePacket.PACKET_TYPE, (packet, ctx) ->
			ScreenShaker.SCREEN_SHAKES.removeIf(
				clientScreenShake -> !(clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake)
			)
		);
	}

	private static void receiveRemoveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(RemoveEntityScreenShakePacket.PACKET_TYPE, (packet, ctx) -> {
			int id = packet.entityId();

			ClientLevel level = ctx.client().level;
            Entity entity = level.getEntity(id);
            if (entity != null) {
                ScreenShaker.SCREEN_SHAKES.removeIf(clientScreenShake -> clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake entityScreenShake && entityScreenShake.getEntity() == entity);
            }
		});
	}

	private static void receiveIconPacket() {
		ClientPlayNetworking.registerGlobalReceiver(SpottingIconPacket.PACKET_TYPE, (packet, ctx) -> {
			int id = packet.entityId();
			ResourceLocation texture = packet.texture();
			float startFade = packet.startFade();
			float endFade = packet.endFade();
			ResourceLocation predicate = packet.restrictionID();

			ClientLevel level = ctx.client().level;
            Entity entity = level.getEntity(id);
            if (entity instanceof EntitySpottingIconInterface livingEntity) {
                livingEntity.getSpottingIconManager().setIcon(texture, startFade, endFade, predicate);
            }
		});
	}

	private static void receiveIconRemovePacket() {
		ClientPlayNetworking.registerGlobalReceiver(SpottingIconRemovePacket.PACKET_TYPE, (packet, ctx) -> {
			int id = packet.entityId();

			ClientLevel level = ctx.client().level;
            Entity entity = level.getEntity(id);
            if (entity instanceof EntitySpottingIconInterface livingEntity) {
                livingEntity.getSpottingIconManager().icon = null;
            }
		});
	}

	private static void receiveWindSyncPacket() {
		ClientPlayNetworking.registerGlobalReceiver(WindSyncPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientWindManager.time = packet.windTime();
			ClientWindManager.setSeed(packet.seed());
			ClientWindManager.overrideWind = packet.override();
			ClientWindManager.commandWind = packet.commandWind();
			ClientWindManager.hasInitialized = true;
		});
	}

	private static void receiveWindDisturbancePacket() {
		ClientPlayNetworking.registerGlobalReceiver(WindDisturbancePacket.PACKET_TYPE, (packet, ctx) -> {
			ClientLevel level = ctx.client().level;
			long posOrID = packet.posOrID();
			Optional<WindDisturbanceLogic> disturbanceLogic = WindDisturbanceLogic.getWindDisturbanceLogic(packet.id());
			if (disturbanceLogic.isPresent()) {
				WindDisturbanceLogic.SourceType sourceType = packet.disturbanceSourceType();
				Optional source = Optional.empty();
				if (sourceType == WindDisturbanceLogic.SourceType.ENTITY) {
					source = Optional.ofNullable(level.getEntity((int) posOrID));
				} else if (sourceType == WindDisturbanceLogic.SourceType.BLOCK_ENTITY) {
					source = Optional.ofNullable(level.getBlockEntity(BlockPos.of(posOrID)));
				}

				ClientWindManager.addWindDisturbance(
					new WindDisturbance(
						source,
						packet.origin(),
						packet.affectedArea(),
						disturbanceLogic.get()
					)
				);
			}
		});
	}

	private static void receiveTransferImagePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE, (packet, ctx) -> {
			if (packet.request()) {
				Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
				try {
					FileTransferPacket fileTransferPacket = FileTransferPacket.create(packet.transferPath(), path.toFile());
					ClientPlayNetworking.send(fileTransferPacket);
				} catch (IOException ignored) {
					FrozenSharedConstants.LOGGER.error("Unable to create and send transfer packet for file {}!", packet.fileName());
				}
			} else {
				try {
					Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
					FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.bytes()), path.toFile());
					ServerTexture serverTexture = ServerTexture.WAITING_TEXTURES.get(packet.transferPath() + "/" + packet.fileName());
					if (serverTexture != null) {
						serverTexture.runFutureForTexture();
					}
				} catch (IOException ignored) {
					FrozenSharedConstants.LOGGER.error("Unable save transferred file {}!", packet.fileName());
				}
			}
		});
	}

	private static void receiveCapePacket() {
		ClientPlayNetworking.registerGlobalReceiver(CapeCustomizePacket.PACKET_TYPE, (packet, ctx) -> {
			UUID uuid = packet.getPlayerUUID();
			if (packet.isEnabled()) {
				ClientCapeData.setCapeForUUID(uuid, packet.getCapeId());
			} else {
				ClientCapeData.removeCapeForUUID(uuid);
			}
		});
	}

	private static void receiveCapeRepoPacket() {
		ClientPlayNetworking.registerGlobalReceiver(LoadCapeRepoPacket.PACKET_TYPE, (packet, ctx) -> {
			CapeUtil.registerCapesFromURL(packet.capeRepo());
		});
	}

	public static boolean notConnected() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return true;

		LocalPlayer player = Minecraft.getInstance().player;
		return player == null;
	}

	public static boolean connectedToLan() {
		if (notConnected()) return false;
		ServerData serverData = Minecraft.getInstance().getCurrentServer();
		if (serverData == null) return false;
		return serverData.isLan();
	}

}
