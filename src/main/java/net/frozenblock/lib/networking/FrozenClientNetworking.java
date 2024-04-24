/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.networking;

import java.util.Optional;
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
		receiveWindDisturbancePacket();
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
		ClientPlayNetworking.registerGlobalReceiver(LocalPlayerSoundPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
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
			LocalPlayer player = Minecraft.getInstance().player;
			Item item = packet.item();
			int additional = packet.additional();
				((CooldownInterface) player.getCooldowns()).frozenLib$changeCooldown(item, additional);
		});
	}

	private static void receiveForcedCooldownPacket() {
		ClientPlayNetworking.registerGlobalReceiver(ForcedCooldownPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
			Item item = packet.item();
			int startTime = packet.startTime();
			int endTime = packet.endTime();
			player.getCooldowns().cooldowns.put(item, new ItemCooldowns.CooldownInstance(startTime, endTime));
		});
	}

	private static void receiveCooldownTickCountPacket() {
		ClientPlayNetworking.registerGlobalReceiver(CooldownTickCountPacket.PACKET_TYPE, (packet, ctx) -> {
			LocalPlayer player = Minecraft.getInstance().player;
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
