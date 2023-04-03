/*
 * Copyright 2023 FrozenBlock
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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.menu.api.Panoramas;
import net.frozenblock.lib.registry.api.client.FrozenClientRegistry;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.frozenblock.lib.sound.api.damagesource.PlayerDamageSourceSounds;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSound;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSoundLoop;
import net.frozenblock.lib.sound.api.instances.RestrictedStartingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.block_sound_group.BlockSoundGroupManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.wind3d.ClientWindManager3D;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

public final class FrozenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		FrozenClientRegistry.initRegistry();
		ModIntegrations.initialize(); // Mod integrations must run after normal mod initialization
		ClientFreezer.onInitializeClient();
		registerClientEvents();

		receiveLocalSoundPacket();
		receiveMovingRestrictionSoundPacket();
		receiveRestrictedMovingSoundLoopPacket();
		receiveStartingRestrictedMovingSoundLoopPacket();
		receiveMovingRestrictionLoopingFadingDistanceSoundPacket();
		receiveMovingFadingDistanceSoundPacket();
		receiveFadingDistanceSoundPacket();
		receiveFlybySoundPacket();
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
		receiveWind3DSyncPacket();
		receivePlayerDamagePacket();
		receiveLocalPlayerSoundPacket();

		Panoramas.addPanorama(new ResourceLocation("textures/gui/title/background/panorama"));

		var resourceLoader = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
		resourceLoader.registerReloadListener(BlockSoundGroupManager.INSTANCE);

		FabricLoader.getInstance().getEntrypointContainers("frozenlib:client", FrozenClientEntrypoint.class).forEach(entrypoint -> {
			try {
				FrozenClientEntrypoint clientPoint = entrypoint.getEntrypoint();
				clientPoint.init();
				if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
					clientPoint.initDevOnly();
				}
			} catch (Throwable ignored) {

			}
		});
	}

	private static void receiveLocalSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.LOCAL_SOUND_PACKET, (client, handler, buf, responseSender) -> {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			SoundEvent sound = buf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource source = buf.readEnum(SoundSource.class);
			float volume = buf.readFloat();
			float pitch = buf.readFloat();
			boolean distanceDelay = buf.readBoolean();
			client.execute(() -> {
				ClientLevel level = client.level;
				if (level != null) {
					level.playLocalSound(x, y, z, sound, source, volume, pitch, distanceDelay);
				}
			});
		});
	}

	private static void receiveLocalPlayerSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.LOCAL_PLAYER_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ctx.execute(() -> {
				if (ctx.level != null) {
					LocalPlayer player = ctx.player;
					if (player != null) {
						assert sound != null;
						ctx.getSoundManager().play(new EntityBoundSoundInstance(sound, SoundSource.PLAYERS, volume, pitch, player, ctx.level.random.nextLong()));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingRestrictionSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingSound<>(entity, sound, category, volume, pitch, predicate));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveRestrictedMovingSoundLoopPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingSoundLoop<>(entity, sound, category, volume, pitch, predicate));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveStartingRestrictedMovingSoundLoopPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent startingSound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent loopingSound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedStartingSound<>(entity, startingSound, loopingSound, category, volume, pitch, predicate, new RestrictedMovingSoundLoop<>(entity, loopingSound, category, volume, pitch, predicate)));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingRestrictionLoopingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, fadeDist, maxDist, volume, false));
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, fadeDist, maxDist, volume, true));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, fadeDist, maxDist, volume, false));
						ctx.getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, fadeDist, maxDist, volume, true));
					}
				}
			});
		});
	}

	private static void receiveFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
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

	private static void receiveFlybySoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FLYBY_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(BuiltInRegistries.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity != null) {
						FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(pitch, volume, category, sound);
						FlyBySoundHub.addEntity(entity, flyBySound);
					}
				}
			});
		});
	}

	private static void receiveCooldownChangePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.COOLDOWN_CHANGE_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			Item item = byteBuf.readById(BuiltInRegistries.ITEM);
			int additional = byteBuf.readVarInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null && ctx.player != null) {
					((CooldownInterface) ctx.player.getCooldowns()).changeCooldown(item, additional);
				}
			});
		});
	}

	private static void receiveForcedCooldownPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FORCED_COOLDOWN_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			Item item = byteBuf.readById(BuiltInRegistries.ITEM);
			int startTime = byteBuf.readVarInt();
			int endTime = byteBuf.readVarInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null && ctx.player != null) {
					ctx.player.getCooldowns().cooldowns.put(item, new ItemCooldowns.CooldownInstance(startTime, endTime));
				}
			});
		});
	}

	private static void receiveCooldownTickCountPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.COOLDOWN_TICK_COUNT_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int tickCount = byteBuf.readInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null && ctx.player != null) {
					ctx.player.getCooldowns().tickCount = tickCount;
				}
			});
		});
	}

	private static void receiveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.SCREEN_SHAKE_PACKET, (ctx, hander, byteBuf, responseSender) -> {
			float intensity = byteBuf.readFloat();
			int duration = byteBuf.readInt();
			int fallOffStart = byteBuf.readInt();
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			float maxDistance = byteBuf.readFloat();
			int ticks = byteBuf.readInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Vec3 pos = new Vec3(x, y, z);
					ScreenShaker.addShake(level, intensity, duration, fallOffStart, pos, maxDistance, ticks);
				}
			});
		});
	}

	private static void receiveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.SCREEN_SHAKE_ENTITY_PACKET, (ctx, hander, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			float intensity = byteBuf.readFloat();
			int duration = byteBuf.readInt();
			int fallOffStart = byteBuf.readInt();
			float maxDistance = byteBuf.readFloat();
			int ticks = byteBuf.readInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity != null) {
						ScreenShaker.addShake(entity, intensity, duration, fallOffStart, maxDistance, ticks);
					}
				}
			});
		});
	}

	private static void receiveRemoveScreenShakePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.REMOVE_SCREEN_SHAKES_PACKET, (ctx, hander, byteBuf, responseSender) -> ctx.execute(() -> ScreenShaker.SCREEN_SHAKES.removeIf(clientScreenShake -> !(clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake))));
	}

	private static void receiveRemoveScreenShakeFromEntityPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.REMOVE_ENTITY_SCREEN_SHAKES_PACKET, (ctx, hander, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity != null) {
						ScreenShaker.SCREEN_SHAKES.removeIf(clientScreenShake -> clientScreenShake instanceof ScreenShaker.ClientEntityScreenShake entityScreenShake && entityScreenShake.getEntity() == entity);
					}
				}
			});
		});
	}

	private static void receiveIconPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.SPOTTING_ICON_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			ResourceLocation texture = byteBuf.readResourceLocation();
			float startFade = byteBuf.readFloat();
			float endFade = byteBuf.readFloat();
			ResourceLocation predicate = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity instanceof EntitySpottingIconInterface livingEntity) {
						livingEntity.getSpottingIconManager().setIcon(texture, startFade, endFade, predicate);
					}
				}
			});
		});
	}

	private static void receiveIconRemovePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.SPOTTING_ICON_REMOVE_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity instanceof EntitySpottingIconInterface livingEntity) {
						livingEntity.getSpottingIconManager().icon = null;
					}
				}
			});
		});
	}

	private static void receiveWindSyncPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.WIND_SYNC_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			long windTime = byteBuf.readLong();
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			long seed = byteBuf.readLong();
			boolean override = byteBuf.readBoolean();
			double xa = byteBuf.readDouble();
			double ya = byteBuf.readDouble();
			double za = byteBuf.readDouble();
			ctx.execute(() -> {
				if (ctx.level != null) {
					ClientWindManager.time = windTime;
					ClientWindManager.cloudX = x;
					ClientWindManager.cloudY = y;
					ClientWindManager.cloudZ = z;
					ClientWindManager.setSeed(seed);
					ClientWindManager.overrideWind = override;
					ClientWindManager.commandWind = new Vec3(xa, ya, za);
					ClientWindManager.hasInitialized = true;
				}
			});
		});
	}

	private static void receiveWind3DSyncPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.WIND_3D_SYNC_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			long windTime = byteBuf.readLong();
			long seed = byteBuf.readLong();
			ctx.execute(() -> {
				if (ctx.level != null) {
					ClientWindManager3D.time = windTime;
					ClientWindManager3D.setSeed(seed);
					ClientWindManager3D.hasInitialized = true;
				}
			});
		});
	}

	private static void receivePlayerDamagePacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.HURT_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			ResourceLocation damageLocation = byteBuf.readResourceLocation();
			float volume = byteBuf.readFloat();
			ctx.execute(() -> {
				ClientLevel level = ctx.level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity instanceof Player player) {
						SoundEvent soundEvent = PlayerDamageSourceSounds.getDamageSound(damageLocation);
						player.playSound(soundEvent, volume, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
					}
				}
			});
		});
	}

	private static void registerClientEvents() {
		ClientTickEvents.START_WORLD_TICK.register(ClientWindManager::tick);
		ClientTickEvents.START_WORLD_TICK.register(ClientWindManager3D::tick);
		ClientTickEvents.START_CLIENT_TICK.register(ScreenShaker::tick);
		ClientTickEvents.START_CLIENT_TICK.register(client -> FlyBySoundHub.update(client, client.player, true));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ScreenShaker.clear());
	}

}
