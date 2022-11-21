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

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.FrozenClientEntrypoint;
import net.frozenblock.lib.impl.PlayerDamageSourceSounds;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.menu.api.Panoramas;
import net.frozenblock.lib.screenshake.ScreenShaker;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.frozenblock.lib.sound.api.FrozenClientPacketInbetween;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSound;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSoundLoop;
import net.frozenblock.lib.sound.api.instances.RestrictedStartingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.wind.ClientWindManager;
import net.frozenblock.lib.wind.WindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.client.ClientFreezer;

public final class FrozenClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientFreezer.onInitializeClient();
		registerClientTickEvents();

		receiveLocalSoundPacket();
		receiveMovingRestrictionSoundPacket();
		receiveRestrictedMovingSoundLoopPacket();
		receiveStartingRestrictedMovingSoundLoopPacket();
		receiveMovingRestrictionLoopingFadingDistanceSoundPacket();
		receiveMovingFadingDistanceSoundPacket();
		receiveFadingDistanceSoundPacket();
		receiveFlybySoundPacket();
		receiveCooldownChangePacket();
		receiveScreenShakePacket();
		receiveScreenShakeFromEntityPacket();
		receiveIconPacket();
		receiveIconRemovePacket();
		receiveWindSyncPacket();
		receivePlayerDamagePacket();

		Panoramas.addPanorama(new ResourceLocation("textures/gui/title/background/panorama"));

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
			SoundEvent sound = buf.readById(Registry.SOUND_EVENT);
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

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingRestrictionSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSound<>(entity, sound, category, volume, pitch, predicate));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveRestrictedMovingSoundLoopPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSoundLoop<>(entity, sound, category, volume, pitch, predicate));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveStartingRestrictedMovingSoundLoopPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.STARTING_RESTRICTION_LOOPING_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent startingSound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundEvent loopingSound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						Minecraft.getInstance().getSoundManager().play(new RestrictedStartingSound<>(entity, startingSound, loopingSound, category, volume, pitch, predicate, new RestrictedMovingSoundLoop<>(entity, loopingSound, category, volume, pitch, predicate)));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingRestrictionLoopingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, fadeDist, maxDist, volume, false));
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, fadeDist, maxDist, volume, true));
					}
				}
			});
		});
	}

	@SuppressWarnings("unchecked")
	private static <T extends Entity> void receiveMovingFadingDistanceSoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.MOVING_FADING_DISTANCE_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ResourceLocation predicateId = byteBuf.readResourceLocation();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					T entity = (T) level.getEntity(id);
					if (entity != null) {
						SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(predicateId);
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound, category, volume, pitch, predicate, fadeDist, maxDist, volume, false));
						Minecraft.getInstance().getSoundManager().play(new RestrictedMovingFadingDistanceSwitchingSoundLoop<>(entity, sound2, category, volume, pitch, predicate, fadeDist, maxDist, volume, true));
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
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundEvent sound2 = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			float fadeDist = byteBuf.readFloat();
			float maxDist = byteBuf.readFloat();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					Minecraft.getInstance().getSoundManager().play(new FadingDistanceSwitchingSound(sound, category, volume, pitch, fadeDist, maxDist, volume, false, x, y, z));
					Minecraft.getInstance().getSoundManager().play(new FadingDistanceSwitchingSound(sound2, category, volume, pitch, fadeDist, maxDist, volume, true, x, y, z));
				}
			});
		});
	}

	private static void receiveFlybySoundPacket() {
		ClientPlayNetworking.registerGlobalReceiver(FrozenMain.FLYBY_SOUND_PACKET, (ctx, handler, byteBuf, responseSender) -> {
			int id = byteBuf.readVarInt();
			SoundEvent sound = byteBuf.readById(Registry.SOUND_EVENT);
			SoundSource category = byteBuf.readEnum(SoundSource.class);
			float volume = byteBuf.readFloat();
			float pitch = byteBuf.readFloat();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
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
			Item item = byteBuf.readById(Registry.ITEM);
			int additional = byteBuf.readVarInt();
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null && Minecraft.getInstance().player != null) {
					((CooldownInterface) Minecraft.getInstance().player.getCooldowns()).changeCooldown(item, additional);
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
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					Vec3 pos = new Vec3(x, y, z);
					ScreenShaker.addShake(intensity, duration, fallOffStart, pos, maxDistance);
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
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity != null) {
						ScreenShaker.addShake(entity, intensity, duration, fallOffStart, maxDistance);
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
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity instanceof LivingEntity livingEntity) {
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
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					Entity entity = level.getEntity(id);
					if (entity instanceof LivingEntity livingEntity) {
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
			ctx.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level != null) {
					ClientWindManager.time = windTime;
					ClientWindManager.cloudX = x;
					ClientWindManager.cloudY = y;
					ClientWindManager.cloudZ = z;
					ClientWindManager.hasSynced = true;
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
				ClientLevel level = Minecraft.getInstance().level;
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

	private static void registerClientTickEvents() {
		ClientTickEvents.START_WORLD_TICK.register(level -> {
			Minecraft client = Minecraft.getInstance();
			if (client.level != null) {
				FlyBySoundHub.update(client, client.player, true);
				if (!ClientWindManager.hasSynced) {
					FrozenClientPacketInbetween.requestWindSync();
				}
				ClientWindManager.tick(level);
			}
		});
		ClientTickEvents.START_CLIENT_TICK.register(level -> {
			Minecraft client = Minecraft.getInstance();
			if (client.level != null) {
				Window window = client.getWindow();
				ScreenShaker.tick(client.gameRenderer.getMainCamera(), client.level.getRandom(), window.getWidth(), window.getHeight());
			}
		});
	}

}
