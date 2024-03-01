/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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
import net.frozenblock.lib.wind.impl.WindSyncPacket;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;

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
		ClientPlayNetworking.registerGlobalReceiver(WindDisturbancePacket.PACKET_TYPE, WindDisturbancePacket::receive);
		registry().register(ConfigSyncPacket.PACKET_TYPE, ConfigSyncPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, (packet, ctx) ->
			ConfigSyncPacket.receive(packet, null)
		);
		ClientConfigurationConnectionEvents.DISCONNECT.register(((handler, client) -> {
			for (Config<?> config : ConfigRegistry.getAllConfigs()) {
				ConfigRegistry.setSyncData(config, null);
				config.setSynced(false);
			}
		}));
	}

	private static void receiveLocalPlayerSoundPacket() {
		registry().register(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(LocalPlayerSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(packet.sound().value(), SoundSource.PLAYERS, packet.volume(), packet.pitch(), player, ctx.client().level.random.nextLong()));
		});
	}

	private static void receiveLocalSoundPacket() {
		registry().register(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(LocalSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientLevel level = ctx.client().level;
			Vec3 pos = packet.pos();
			level.playLocalSound(pos.x, pos.y, pos.z, packet.sound().value(), packet.category(), packet.volume(), packet.pitch(), packet.distanceDelay());
		});
	}

	private static <T extends Entity> void receiveStartingMovingRestrictionSoundLoopPacket() {
		registry().register(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
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
		registry().register(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
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
		registry().register(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			ctx.client().getSoundManager().play(new FadingDistanceSwitchingSound(packet.closeSound().value(), packet.category(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), false, packet.pos()));
			ctx.client().getSoundManager().play(new FadingDistanceSwitchingSound(packet.farSound().value(), packet.category(), packet.volume(), packet.pitch(), packet.fadeDist(), packet.maxDist(), packet.volume(), true, packet.pos()));
		});
	}

	private static <T extends Entity> void receiveMovingFadingDistanceSwitchingSoundPacket() {
		registry().register(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);
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
		registry().register(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(CooldownChangePacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			Item item = packet.item();
			int additional = packet.additional();
				((CooldownInterface) player.getCooldowns()).frozenLib$changeCooldown(item, additional);
		});
	}

	private static void receiveForcedCooldownPacket() {
		registry().register(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			Item item = packet.item();
			int startTime = packet.startTime();
			int endTime = packet.endTime();
			player.getCooldowns().cooldowns.put(item, new ItemCooldowns.CooldownInstance(startTime, endTime));
		});
	}

	private static void receiveCooldownTickCountPacket() {
		registry().register(CooldownTickCountPacket.PACKET_TYPE, CooldownTickCountPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(CooldownTickCountPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				player.getCooldowns().tickCount = packet.count();
			}
		});
	}

	private static void receiveScreenShakePacket() {
		registry().register(ScreenShakePacket.PACKET_TYPE, ScreenShakePacket.CODEC);
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
		registry().register(EntityScreenShakePacket.PACKET_TYPE, EntityScreenShakePacket.CODEC);
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
		registry().register(RemoveScreenShakePacket.PACKET_TYPE, RemoveScreenShakePacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(RemoveScreenShakePacket.PACKET_TYPE, (packet, ctx) ->
			ScreenShaker.SCREEN_SHAKES.removeIf(
				clientScreenShake -> !(clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake)
			)
		);
	}

	private static void receiveRemoveScreenShakeFromEntityPacket() {
		registry().register(RemoveEntityScreenShakePacket.PACKET_TYPE, RemoveEntityScreenShakePacket.CODEC);
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
		registry().register(SpottingIconPacket.PACKET_TYPE, SpottingIconPacket.CODEC);
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
		registry().register(SpottingIconRemovePacket.PACKET_TYPE, SpottingIconRemovePacket.CODEC);
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
		registry().register(WindSyncPacket.PACKET_TYPE, WindSyncPacket.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(WindSyncPacket.PACKET_TYPE, (packet, ctx) -> {
			ClientWindManager.time = packet.windTime();
			ClientWindManager.setSeed(packet.seed());
			ClientWindManager.overrideWind = packet.override();
			ClientWindManager.commandWind = packet.commandWind();
			ClientWindManager.hasInitialized = true;
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
