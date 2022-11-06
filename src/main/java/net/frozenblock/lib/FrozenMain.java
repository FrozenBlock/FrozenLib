/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.FrozenMainEntrypoint;
import net.frozenblock.lib.math.EasyNoiseSampler;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.frozenblock.lib.sound.api.FrozenSoundPackets;
import net.frozenblock.lib.sound.api.MovingLoopingFadingDistanceSoundEntityManager;
import net.frozenblock.lib.sound.api.MovingLoopingSoundEntityManager;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.EntityLoopingFadingDistanceSoundInterface;
import net.frozenblock.lib.sound.impl.EntityLoopingSoundInterface;
import net.frozenblock.lib.spotting_icons.SpottingIconPredicate;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;
import org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl.QuiltSurfaceRuleInitializer;
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
        QuiltSurfaceRuleInitializer.onInitialize();
        SoundPredicate.init();
		SpottingIconPredicate.init();

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

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			if (server != null) {
				var seed = server.overworld().getSeed();
				if (EasyNoiseSampler.seed != seed) {
					EasyNoiseSampler.setSeed(seed);
				}
			}
		});
    }

    //IDENTIFIERS
    public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_LOOPING_SOUND_PACKET = id("moving_restriction_looping_sound_packet");
    public static final ResourceLocation STARTING_RESTRICTION_LOOPING_SOUND_PACKET = id("starting_moving_restriction_looping_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");
    public static final ResourceLocation MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET = id("moving_restriction_looping_fading_distance_sound_packet");
    public static final ResourceLocation FADING_DISTANCE_SOUND_PACKET = id("fading_distance_sound_packet");
    public static final ResourceLocation MOVING_FADING_DISTANCE_SOUND_PACKET = id("moving_fading_distance_sound_packet");
    public static final ResourceLocation COOLDOWN_CHANGE_PACKET = id("cooldown_change_packet");
    public static final ResourceLocation REQUEST_LOOPING_SOUND_SYNC_PACKET = id("request_looping_sound_sync_packet");

	public static final ResourceLocation SCREEN_SHAKE_PACKET = id("screen_shake_packet");

	public static final ResourceLocation SPOTTING_ICON_PACKET = id("spotting_icon_packet");
	public static final ResourceLocation SPOTTING_ICON_REMOVE_PACKET = id("spotting_icon_remove_packet");
	public static final ResourceLocation REQUEST_SPOTTING_ICON_SYNC_PACKET = id("request_spotting_icon_sync_packet");

	public static final ResourceLocation HURT_SOUND_PACKET = id("hurt_sound_packet");

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

    private static void receiveSoundSyncPacket() {
        ServerPlayNetworking.registerGlobalReceiver(FrozenMain.REQUEST_LOOPING_SOUND_SYNC_PACKET, (ctx, player, handler, byteBuf, responseSender) -> {
            int id = byteBuf.readVarInt();
            Level dimension = ctx.getLevel(byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY));
            ctx.execute(() -> {
                if (dimension != null) {
                    Entity entity = dimension.getEntity(id);
                    if (entity != null) {
                        if (entity instanceof EntityLoopingSoundInterface soundInterface) {
                            for (MovingLoopingSoundEntityManager.SoundLoopData nbt : soundInterface.getSounds().getSounds()) {
                                FrozenSoundPackets.createMovingRestrictionLoopingSound(player, entity, Registry.SOUND_EVENT.get(nbt.getSoundEventID()), SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()), nbt.volume, nbt.pitch, nbt.restrictionID);
                            }
                        }
						if (entity instanceof EntityLoopingFadingDistanceSoundInterface soundInterface) {
							for (MovingLoopingFadingDistanceSoundEntityManager.FadingDistanceSoundLoopNBT nbt : soundInterface.getFadingDistanceSounds().getSounds()) {
								FrozenSoundPackets.createMovingRestrictionLoopingFadingDistanceSound(player, entity, Registry.SOUND_EVENT.get(nbt.getSoundEventID()), Registry.SOUND_EVENT.get(nbt.getSound2EventID()), SoundSource.valueOf(SoundSource.class, nbt.getOrdinal()), nbt.volume, nbt.pitch, nbt.restrictionID, nbt.fadeDist, nbt.maxDist);
							}
						}
                    }
                }
            });
        });
    }

	private static void receiveIconSyncPacket() {
		ServerPlayNetworking.registerGlobalReceiver(FrozenMain.REQUEST_SPOTTING_ICON_SYNC_PACKET, (ctx, player, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			Level dimension = ctx.getLevel(byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY));
			ctx.execute(() -> {
				if (dimension != null) {
					Entity entity = dimension.getEntity(id);
					if (entity != null) {
						if (entity instanceof EntitySpottingIconInterface icon) {
							icon.getSpottingIconManager().sendIconPacket(player);
						}
					}
				}
			});
		});
	}

}
