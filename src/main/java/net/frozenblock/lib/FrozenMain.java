/*
 * Copyright 2022 FrozenBlock
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

package net.frozenblock.lib;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.event.api.PlayerJoinEvent;
import net.frozenblock.lib.feature.FrozenFeatures;
import net.frozenblock.lib.impl.PlayerDamageSourceSounds;
import net.frozenblock.lib.math.api.EasyNoiseSampler;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.frozenblock.lib.sound.api.FrozenSoundPackets;
import net.frozenblock.lib.sound.api.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.command.OverrideWindCommand;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public final class FrozenMain implements ModInitializer {
	public static final String MOD_ID = "frozenlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final NOPLogger LOGGER4 = NOPLogger.NOP_LOGGER;
	public static boolean DEV_LOGGING = false;

	/**
	 * Used for features that may be unstable and crash in public builds.
	 * <p>
	 * It's smart to use this for at least registries.
	 */
	public static boolean UNSTABLE_LOGGING = FabricLoader.getInstance().isDevelopmentEnvironment();

	@Override
	public void onInitialize() {
		FrozenRegistry.initRegistry();
		ServerFreezer.onInitialize();
		SoundPredicate.init();
		SpottingIconPredicate.init();
		FrozenFeatures.init();

		receiveSoundSyncPacket();
		receiveIconSyncPacket();

		FabricLoader.getInstance().getEntrypointContainers("frozenlib:main", FrozenMainEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenMainEntrypoint mainPoint = entrypoint.getEntrypoint();
				mainPoint.init();
				if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
					mainPoint.initDevOnly();
				}
			} catch (Throwable ignored) {

			}
		});

		PlayerDamageSourceSounds.addDamageSound(DamageSource.DROWN, SoundEvents.PLAYER_HURT_DROWN, FrozenMain.id("player_drown"));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> OverrideWindCommand.register(dispatcher));

		ServerWorldEvents.LOAD.register((server, level) -> {
			if (server != null) {
				var seed = server.overworld().getSeed();
				EasyNoiseSampler.setSeed(seed);
				WindManager.setSeed(seed);
			}
		});

		ServerTickEvents.START_SERVER_TICK.register((server) -> WindManager.tick(server, server.overworld()));

		PlayerJoinEvent.register(((server, player) -> {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeLong(WindManager.time);
			byteBuf.writeDouble(WindManager.cloudX);
			byteBuf.writeDouble(WindManager.cloudY);
			byteBuf.writeDouble(WindManager.cloudZ);
			byteBuf.writeLong(server.overworld().getSeed());
			byteBuf.writeBoolean(WindManager.overrideWind);
			byteBuf.writeDouble(WindManager.commandWind.x());
			byteBuf.writeDouble(WindManager.commandWind.y());
			byteBuf.writeDouble(WindManager.commandWind.z());
			ServerPlayNetworking.send(player, FrozenMain.WIND_SYNC_PACKET, byteBuf);
		}));

	}

	//IDENTIFIERS
	public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
	public static final ResourceLocation LOCAL_SOUND_PACKET = id("local_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_LOOPING_SOUND_PACKET = id("moving_restriction_looping_sound_packet");
	public static final ResourceLocation STARTING_RESTRICTION_LOOPING_SOUND_PACKET = id("starting_moving_restriction_looping_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET = id("moving_restriction_looping_fading_distance_sound_packet");
	public static final ResourceLocation FADING_DISTANCE_SOUND_PACKET = id("fading_distance_sound_packet");
	public static final ResourceLocation MOVING_FADING_DISTANCE_SOUND_PACKET = id("moving_fading_distance_sound_packet");
	public static final ResourceLocation LOCAL_PLAYER_SOUND_PACKET = id("local_player_sound_packet");
	public static final ResourceLocation COOLDOWN_CHANGE_PACKET = id("cooldown_change_packet");
	public static final ResourceLocation REQUEST_LOOPING_SOUND_SYNC_PACKET = id("request_looping_sound_sync_packet");

	public static final ResourceLocation SCREEN_SHAKE_PACKET = id("screen_shake_packet");
	public static final ResourceLocation SCREEN_SHAKE_ENTITY_PACKET = id("screen_shake_entity_packet");

	public static final ResourceLocation SPOTTING_ICON_PACKET = id("spotting_icon_packet");
	public static final ResourceLocation SPOTTING_ICON_REMOVE_PACKET = id("spotting_icon_remove_packet");
	public static final ResourceLocation REQUEST_SPOTTING_ICON_SYNC_PACKET = id("request_spotting_icon_sync_packet");

	public static final ResourceLocation HURT_SOUND_PACKET = id("hurt_sound_packet");

	public static final ResourceLocation WIND_SYNC_PACKET = id("wind_sync_packet");
	public static final ResourceLocation SMALL_WIND_SYNC_PACKET = id("small_wind_sync_packet");

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static String string(String path) {
		return id(path).toString();
	}

	public static void log(String string, boolean should) {
		if (should) {
			LOGGER.info(string);
		}
	}

	public static void warn(String string, boolean should) {
		if (should) {
			LOGGER.warn(string);
		}
	}

	public static void error(String string, boolean should) {
		if (should) {
			LOGGER.error(string);
		}
	}

	private static void receiveSoundSyncPacket() {
		ServerPlayNetworking.registerGlobalReceiver(FrozenMain.REQUEST_LOOPING_SOUND_SYNC_PACKET, (ctx, player, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			Level dimension = ctx.getLevel(byteBuf.readResourceKey(Registries.DIMENSION));
			ctx.execute(() -> {
				if (dimension != null) {
					Entity entity = dimension.getEntity(id);
					if (entity instanceof LivingEntity livingEntity) {
						for (MovingLoopingSoundEntityManager.SoundLoopData nbt : ((EntityLoopingSoundInterface)livingEntity).getSounds().getSounds()) {
							FrozenSoundPackets.createMovingRestrictionLoopingSound(player, entity, BuiltInRegistries.SOUND_EVENT.get(nbt.getSoundEventID()), SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()), nbt.volume, nbt.pitch, nbt.restrictionID);
						}
						for (MovingLoopingFadingDistanceSoundEntityManager.FadingDistanceSoundLoopNBT nbt : ((EntityLoopingFadingDistanceSoundInterface)livingEntity).getFadingDistanceSounds().getSounds()) {
							FrozenSoundPackets.createMovingRestrictionLoopingFadingDistanceSound(player, entity, BuiltInRegistries.SOUND_EVENT.get(nbt.getSoundEventID()), BuiltInRegistries.SOUND_EVENT.get(nbt.getSound2EventID()), SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()), nbt.volume, nbt.pitch, nbt.restrictionID, nbt.fadeDist, nbt.maxDist);
						}
					}
				}
			});
		});
	}

	private static void receiveIconSyncPacket() {
		ServerPlayNetworking.registerGlobalReceiver(FrozenMain.REQUEST_SPOTTING_ICON_SYNC_PACKET, (ctx, player, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			Level dimension = ctx.getLevel(byteBuf.readResourceKey(Registries.DIMENSION));
			ctx.execute(() -> {
				if (dimension != null) {
					Entity entity = dimension.getEntity(id);
					if (entity != null) {
						if (entity instanceof EntitySpottingIconInterface livingEntity) {
							livingEntity.getSpottingIconManager().sendIconPacket(player);
						}
					}
				}
			});
		});
	}
}
