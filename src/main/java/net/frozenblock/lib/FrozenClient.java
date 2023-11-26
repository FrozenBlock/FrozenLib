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
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.network.ConfigSyncPacket;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.entrypoint.api.FrozenClientEntrypoint;
import net.frozenblock.lib.integration.api.ModIntegrations;
import net.frozenblock.lib.item.impl.CooldownInterface;
import net.frozenblock.lib.menu.api.Panoramas;
import net.frozenblock.lib.registry.api.client.FrozenClientRegistry;
import net.frozenblock.lib.screenshake.api.client.ScreenShaker;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.frozenblock.lib.sound.api.instances.distance_based.FadingDistanceSwitchingSound;
import net.frozenblock.lib.sound.api.instances.distance_based.RestrictedMovingFadingDistanceSwitchingSoundLoop;
import net.frozenblock.lib.sound.api.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.api.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.api.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.api.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.sound.impl.block_sound_group.BlockSoundGroupManager;
import net.frozenblock.lib.spotting_icons.impl.EntitySpottingIconInterface;
import net.frozenblock.lib.wind.api.ClientWindManager;
import net.frozenblock.lib.wind.api.ClientWindManagerExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
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
		ClientPlayNetworking.registerGlobalReceiver(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket::receive);
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, (packet, player, responseSender) ->
			ConfigSyncPacket.receive(packet, null)
		);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
				for (Config<?> config : ConfigRegistry.getAllConfigs()) {
					ConfigRegistry.setSyncData(config, null);
				}
		});

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

	private static void registerClientEvents() {
		ClientTickEvents.START_WORLD_TICK.register(ClientWindManager::tick);
		ClientTickEvents.START_CLIENT_TICK.register(ScreenShaker::tick);
		ClientTickEvents.START_CLIENT_TICK.register(client -> FlyBySoundHub.update(client, client.getCameraEntity(), true));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ScreenShaker.clear());
	}

}
