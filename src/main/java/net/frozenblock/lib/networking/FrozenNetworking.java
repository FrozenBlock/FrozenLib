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

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.event.api.PlayerJoinEvents;
import net.frozenblock.lib.wind.api.WindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;

public final class FrozenNetworking {
	private FrozenNetworking() {}

	public static final ResourceLocation STARTING_RESTRICTION_LOOPING_SOUND_PACKET = FrozenSharedConstants.id("starting_moving_restriction_looping_sound_packet");
	public static final ResourceLocation MOVING_RESTRICTION_LOOPING_FADING_DISTANCE_SOUND_PACKET = FrozenSharedConstants.id("moving_restriction_looping_fading_distance_sound_packet");
	public static final ResourceLocation FADING_DISTANCE_SOUND_PACKET = FrozenSharedConstants.id("fading_distance_sound_packet");
	public static final ResourceLocation MOVING_FADING_DISTANCE_SOUND_PACKET = FrozenSharedConstants.id("moving_fading_distance_sound_packet");

	public static final ResourceLocation WIND_SYNC_PACKET = FrozenSharedConstants.id("wind_sync_packet");

	public static void registerNetworking() {
		PlayerJoinEvents.ON_PLAYER_ADDED_TO_LEVEL.register(((server, serverLevel, player) -> {
			WindManager windManager = WindManager.getWindManager(serverLevel);
			windManager.sendSyncToPlayer(windManager.createSyncByteBuf(), player);
		}));

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigSyncPacket.sendS2C(player);
		});

		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, error) -> {
			if (error != null || server == null) return;
			for (ServerPlayer player : PlayerLookup.all(server)) {
				ConfigSyncPacket.sendS2C(player);
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, ((packet, player, sender) -> {
			if (ConfigSyncPacket.hasPermissionsToSendSync(player, true))
				ConfigSyncPacket.receive(packet, player.server);
		}));
	}

	public static boolean isLocalPlayer(Player player) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return false;

		return Minecraft.getInstance().isLocalPlayer(player.getGameProfile().getId());
	}

	public static boolean connectedToIntegratedServer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return false;
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.hasSingleplayerServer();
	}

	/**
	 * @return if the client is connected to any server
	 */
	public static boolean connectedToServer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return false;
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null)
			return false;
		return listener.getConnection().isConnected();
	}

	/**
	 * @return if the current server is multiplayer (LAN/dedicated) or not (singleplayer)
	 */
	public static boolean isMultiplayer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return true;

		return !Minecraft.getInstance().isSingleplayer();
	}

}
