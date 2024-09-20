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

import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.client.impl.ClientCapeData;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.screenshake.impl.network.EntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveEntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.ScreenShakePacket;
import net.frozenblock.lib.sound.api.instances.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.api.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconRemovePacket;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.ClientWindManagerExtension;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class FrozenClientNetworking {

	public static void registerClientReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket::receive);
		receiveMovingRestrictionLoopingFadingDistanceSoundPacket();
		receiveMovingFadingDistanceSoundPacket();
		receiveFadingDistanceSoundPacket();
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
		ClientPlayNetworking.registerGlobalReceiver(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket::receive);
		receiveCapePacket();
		receiveCapeRepoPacket();
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, (packet, ctx, sender) ->
			ConfigSyncPacket.receive(packet, null)
		);
		ClientConfigurationConnectionEvents.DISCONNECT.register(((handler, client) -> {
			for (Config<?> config : ConfigRegistry.getAllConfigs()) {
				ConfigRegistry.setSyncData(config, null);
				config.setSynced(false);
			}
		}));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingRestrictionLoopingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenNetworking.MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			boolean stopOnDeath = byteBuf.readBoolean();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, stopOnDeath, fadeDist, maxDist, volume, false));
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, stopOnDeath, fadeDist, maxDist, volume, true));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenNetworking.MOVING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			boolean stopOnDeath = byteBuf.readBoolean();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, stopOnDeath, fadeDist, maxDist, volume, false));
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, stopOnDeath, fadeDist, maxDist, volume, true));
					}
				}
			});
		});
	}

	private static void receiveFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenNetworking.FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					ctx.getSoundManager().play(new FadingDistanceSwitchingSound(sound, category, volume, pitch, fadeDist, maxDist, volume, false, x, y, z));
					ctx.getSoundManager().play(new FadingDistanceSwitchingSound(sound2, category, volume, pitch, fadeDist, maxDist, volume, true, x, y, z));
				}
			});
		});
	}

	private static void receiveCooldownChangePacket() {
		ClientPlayNetworking.registerGlobalReceiver(CooldownChangePacket.PACKET_TYPE, (packet, player, responseSender) -> {
			Item item = packet.item();
			int additional = packet.additional();
			if (player != null) {
				((CooldownInterface) player.getCooldowns()).frozenLib$changeCooldown(item, additional);
			}
		});
	}

	private static void receiveForcedCooldownPacket() {
		ClientPlayNetworking.registerGlobalReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, player, responseSender) -> {
			Item item = packet.item();
			int startTime = packet.startTime();
			int endTime = packet.endTime();
			if (player != null) {
				player.getCooldowns().cooldowns.put(item, new ItemCooldowns.CooldownInstance(startTime, endTime));
			}
		});
	}

	private static void receiveCooldownTickCountPacket() {
		ClientPlayNetworking.registerGlobalReceiver(CooldownTickCountPacket.PACKET_TYPE, (packet, player, responseSender) -> {
			if (player != null) {
				player.getCooldowns().tickCount = packet.count();
			}
		});
	}

	private static void receiveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenShakePacket.PACKET_TYPE, (packet, player, responseSender) -> {
			float intensity = packet.intensity();
			int duration = packet.duration();
			int fallOffStart = packet.falloffStart();
			double x = packet.x();
			double y = packet.y();
			double z = packet.z();
			float maxDistance = packet.maxDistance();
			int ticks = packet.ticks();

			ClientLevel level = player.clientLevel;
            Vec3 pos = new Vec3(x, y, z);
            ScreenShaker.addShake(level, intensity, duration, fallOffStart, pos, maxDistance, ticks);
        });
	}

	private static void receiveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(EntityScreenShakePacket.PACKET_TYPE, (packet, player, responseSender) -> {
			int id = packet.entityId();
			float intensity = packet.intensity();
			int duration = packet.duration();
			int fallOffStart = packet.falloffStart();
			float maxDistance = packet.maxDistance();
			int ticks = packet.ticks();

			ClientLevel level = player.clientLevel;
            Entity entity = level.getEntity(id);
            if (entity != null) {
                ScreenShaker.addShake(entity, intensity, duration, fallOffStart, maxDistance, ticks);
            }
		});
	}

	private static void receiveRemoveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(RemoveScreenShakePacket.PACKET_TYPE, (packet, player, responseSender) ->
			ScreenShaker.SCREEN_SHAKES.removeIf(
				clientScreenShake -> !(clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake)
			)
		);
	}

	private static void receiveRemoveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(RemoveEntityScreenShakePacket.PACKET_TYPE, (packet, player, responseSender) -> {
			int id = packet.entityId();

			ClientLevel level = player.clientLevel;
            Entity entity = level.getEntity(id);
            if (entity != null) {
                ScreenShaker.SCREEN_SHAKES.removeIf(clientScreenShake -> clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake entityScreenShake && entityScreenShake.getEntity() == entity);
            }
		});
	}

	private static void receiveIconPacket() {
		ClientPlayNetworking.registerGlobalReceiver(SpottingIconPacket.PACKET_TYPE, (packet, player, responseSender) -> {
			int id = packet.entityId();
			ResourceLocation texture = packet.texture();
			float startFade = packet.startFade();
			float endFade = packet.endFade();
			ResourceLocation predicate = packet.restrictionID();

			ClientLevel level = player.clientLevel;
            Entity entity = level.getEntity(id);
            if (entity instanceof EntitySpottingIconInterface livingEntity) {
                livingEntity.getSpottingIconManager().setIcon(texture, startFade, endFade, predicate);
            }
		});
	}

	private static void receiveIconRemovePacket() {
		ClientPlayNetworking.registerGlobalReceiver(SpottingIconRemovePacket.PACKET_TYPE, (packet, player, responseSender) -> {
			int id = packet.entityId();

			ClientLevel level = player.clientLevel;
            Entity entity = level.getEntity(id);
            if (entity instanceof EntitySpottingIconInterface livingEntity) {
                livingEntity.getSpottingIconManager().icon = null;
            }
		});
	}

	private static void receiveWindSyncPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenNetworking.WIND_SYNC_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			long windTime = byteBuf.readLong();
			long seed = byteBuf.readLong();
			boolean override = byteBuf.readBoolean();
			double commandX = byteBuf.readDouble();
			double commandY = byteBuf.readDouble();
			double commandZ = byteBuf.readDouble();
			ctx.execute(() -> {
				if (ctx.level != null) {
					ClientWindManager.time = windTime;
					ClientWindManager.setSeed(seed);
					ClientWindManager.overrideWind = override;
					ClientWindManager.commandWind = new Vec3(commandX, commandY, commandZ);
					ClientWindManager.hasInitialized = true;
				}
			});

			// EXTENSIONS
			for (ClientWindManagerExtension extension : ClientWindManager.EXTENSIONS) extension.receiveSyncPacket(byteBuf, ctx);
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void receiveWindDisturbancePacket() {
		ClientPlayNetworking.registerGlobalReceiver(WindDisturbancePacket.PACKET_TYPE, (packet, player, sender) -> {
			ClientLevel level = player.clientLevel;
			long posOrID = packet.posOrID();
			Optional<WindDisturbanceLogic<?>> disturbanceLogic = WindDisturbanceLogic.getWindDisturbanceLogic(packet.id());
			if (disturbanceLogic.isPresent()) {
				WindDisturbanceLogic.SourceType sourceType = packet.disturbanceSourceType();
				Optional<?> source = Optional.empty();
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

	private static void receiveCapePacket() {
		ClientPlayNetworking.registerGlobalReceiver(CapeCustomizePacket.PACKET_TYPE, (packet, ctx, sender) -> {
			UUID uuid = packet.getPlayerUUID();
			if (packet.isEnabled()) {
				ClientCapeData.setCapeForUUID(uuid, packet.getCapeId());
			} else {
				ClientCapeData.removeCapeForUUID(uuid);
			}
		});
	}

	private static void receiveCapeRepoPacket() {
		ClientPlayNetworking.registerGlobalReceiver(LoadCapeRepoPacket.PACKET_TYPE, (packet, ctx, sender) -> {
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
