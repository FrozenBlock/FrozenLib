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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.network.ConfigSyncPacket;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.ConfigCommand;
import net.frozenblock.lib.core.impl.DataPackReloadMarker;
import net.frozenblock.lib.entrypoint.api.FrozenMainEntrypoint;
import net.frozenblock.lib.event.api.PlayerJoinEvents;
import net.frozenblock.lib.ingamedevtools.RegisterInGameDevTools;
import net.frozenblock.lib.registry.api.FrozenRegistry;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.frozenblock.lib.screenshake.api.command.ScreenShakeCommand;
import net.frozenblock.lib.screenshake.impl.ScreenShakeStorage;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.spotting_icons.api.SpottingIconPredicate;
import net.frozenblock.lib.tag.api.TagKeyArgument;
import net.frozenblock.lib.tag.api.TagListCommand;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.api.command.WindOverrideCommand;
import net.frozenblock.lib.wind.impl.WindStorage;
import net.frozenblock.lib.worldgen.feature.api.FrozenFeatures;
import net.frozenblock.lib.worldgen.feature.api.placementmodifier.FrozenPlacementModifiers;
import net.frozenblock.lib.worldgen.surface.impl.BiomeTagConditionSource;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.ServerFreezer;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;
import java.util.List;

public final class FrozenMain implements ModInitializer {

	@Override
	public void onInitialize() {
		FrozenRegistry.initRegistry();
		ServerFreezer.onInitialize();
		SoundPredicate.init();
		SpottingIconPredicate.init();
		FrozenFeatures.init();
		FrozenPlacementModifiers.init();
		DataPackReloadMarker.init();

		Registry.register(BuiltInRegistries.MATERIAL_CONDITION, FrozenMain.id("biome_tag_condition_source"), BiomeTagConditionSource.CODEC.codec());

		RegisterInGameDevTools.register();

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

		ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, ((packet, player, responseSender) -> ConfigSyncPacket.receive(packet)));

		ArgumentTypeInfos.register(
			BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
			string("tag_key"),
			ArgumentTypeInfos.fixClassType(TagKeyArgument.class),
			new TagKeyArgument.Info<>()
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			WindOverrideCommand.register(dispatcher);
			ScreenShakeCommand.register(dispatcher);
			ConfigCommand.register(dispatcher);
			TagListCommand.register(dispatcher);
		});

		if (FrozenLibConfig.get().wardenSpawnTrackerCommand)
			CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> WardenSpawnTrackerCommand.register(dispatcher)));

		ServerWorldEvents.LOAD.register((server, level) -> {
			DimensionDataStorage dimensionDataStorage = level.getDataStorage();
			WindManager windManager = WindManager.getWindManager(level);
			dimensionDataStorage.computeIfAbsent(windManager.createData(), WindStorage.WIND_FILE_ID);
			ScreenShakeManager screenShakeManager = ScreenShakeManager.getScreenShakeManager(level);
			dimensionDataStorage.computeIfAbsent(screenShakeManager.createData(), ScreenShakeStorage.SCREEN_SHAKE_FILE_ID);
		});

		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			WindManager.getWindManager(serverLevel).tick();
			ScreenShakeManager.getScreenShakeManager(serverLevel).tick();
		});

		PlayerJoinEvents.ON_PLAYER_ADDED_TO_LEVEL.register(((server, serverLevel, player) -> {
			WindManager windManager = WindManager.getWindManager(serverLevel);
			windManager.sendSyncToPlayer(windManager.createSyncByteBuf(), player);
		}));

		PlayerJoinEvents.ON_JOIN_SERVER.register(((server, player) -> {
			sendConfigSyncPacket(player);
		}));

		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, error) -> {
			if (error != null || server == null) return;
			for (ServerPlayer player : PlayerLookup.all(server)) {
				sendConfigSyncPacket(player);
			}
		});
	}

	private static void sendConfigSyncPacket(ServerPlayer player) {
		for (Config<?> config : ConfigRegistry.getAllConfigs()) {
			if (!config.supportsModification()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.config());
			ServerPlayNetworking.send(player, packet);
		}
	}

	//IDENTIFIERS
	public static final ResourceLocation FLYBY_SOUND_PACKET = id("flyby_sound_packet");
	public static final ResourceLocation LOCAL_SOUND_PACKET = id("local_sound_packet");
	public static final ResourceLocation STARTING_RESTRICTION_LOOPING_SOUND_PACKET = id("starting_moving_restriction_looping_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_SOUND_PACKET = id("moving_restriction_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET = id("moving_restriction_looping_fading_distance_sound_packet");
	public static final ResourceLocation FADING_DISTANCE_SOUND_PACKET = id("fading_distance_sound_packet");
	public static final ResourceLocation MOVING_FADING_DISTANCE_SOUND_PACKET = id("moving_fading_distance_sound_packet");
	public static final ResourceLocation LOCAL_PLAYER_SOUND_PACKET = id("local_player_sound_packet");
	public static final ResourceLocation COOLDOWN_CHANGE_PACKET = id("cooldown_change_packet");
	public static final ResourceLocation FORCED_COOLDOWN_PACKET = id("forced_cooldown_packet");
	public static final ResourceLocation COOLDOWN_TICK_COUNT_PACKET = id("cooldown_tick_count_packet");

	public static final ResourceLocation SCREEN_SHAKE_PACKET = id("screen_shake_packet");
	public static final ResourceLocation SCREEN_SHAKE_ENTITY_PACKET = id("screen_shake_entity_packet");
	public static final ResourceLocation REMOVE_SCREEN_SHAKES_PACKET = id("remove_screen_shakes_packet");
	public static final ResourceLocation REMOVE_ENTITY_SCREEN_SHAKES_PACKET = id("remove_entity_screen_shakes_packet");

	public static final ResourceLocation SPOTTING_ICON_PACKET = id("spotting_icon_packet");
	public static final ResourceLocation SPOTTING_ICON_REMOVE_PACKET = id("spotting_icon_remove_packet");

	public static final ResourceLocation WIND_SYNC_PACKET = id("wind_sync_packet");

	public static final ResourceLocation CONFIG_SYNC_PACKET = id("config_sync_packet");

	public static ResourceLocation id(String path) {
		return new ResourceLocation(FrozenSharedConstants.MOD_ID, path);
	}

	public static String string(String path) {
		return id(path).toString();
	}

}
