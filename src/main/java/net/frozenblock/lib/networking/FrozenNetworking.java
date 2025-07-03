/*
 * Copyright (C) 2024-2025 FrozenBlock
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.ServerCapeData;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.debug.networking.GoalDebugRemovePayload;
import net.frozenblock.lib.debug.networking.ImprovedGameEventDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGameEventListenerDebugPayload;
import net.frozenblock.lib.debug.networking.ImprovedGoalDebugPayload;
import net.frozenblock.lib.debug.networking.StructureDebugRequestPayload;
import net.frozenblock.lib.event.api.PlayerJoinEvents;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.frozenblock.lib.file.transfer.FileTransferPacket;
import net.frozenblock.lib.file.transfer.FileTransferRebuilder;
import net.frozenblock.lib.item.impl.network.CooldownChangePacket;
import net.frozenblock.lib.item.impl.network.CooldownTickCountPacket;
import net.frozenblock.lib.item.impl.network.ForcedCooldownPacket;
import net.frozenblock.lib.screenshake.impl.network.EntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveEntityScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.RemoveScreenShakePacket;
import net.frozenblock.lib.screenshake.impl.network.ScreenShakePacket;
import net.frozenblock.lib.sound.impl.networking.FadingDistanceSwitchingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.FlyBySoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalPlayerSoundPacket;
import net.frozenblock.lib.sound.impl.networking.LocalSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingFadingDistanceSwitchingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.MovingRestrictionSoundPacket;
import net.frozenblock.lib.sound.impl.networking.RelativeMovingSoundPacket;
import net.frozenblock.lib.sound.impl.networking.StartingMovingRestrictionSoundLoopPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconRemovePacket;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.impl.networking.WindAccessPacket;
import net.frozenblock.lib.wind.impl.networking.WindDisturbancePacket;
import net.frozenblock.lib.wind.impl.networking.WindSyncPacket;
import net.frozenblock.lib.worldgen.structure.impl.status.networking.PlayerStructureStatusPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.frozenblock.resource.loader.api.ResourceLoaderEvents;

public final class FrozenNetworking {
	private FrozenNetworking() {}

	public static void registerNetworking() {
		PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.playS2C();
		PayloadTypeRegistry<RegistryFriendlyByteBuf> c2sRegistry = PayloadTypeRegistry.playC2S();

		PlayerJoinEvents.ON_PLAYER_ADDED_TO_LEVEL.register(((server, serverLevel, player) -> {
			WindManager windManager = WindManager.getOrCreateWindManager(serverLevel);
			windManager.sendSyncToPlayer(windManager.createSyncPacket(), player);
			ServerCapeData.sendAllCapesToPlayer(player);
		}));

		PlayerJoinEvents.ON_JOIN_SERVER.register((server, player) -> {
			ConfigSyncPacket.sendS2C(player);
			ServerCapeData.sendCapeReposToPlayer(player);
		});

		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, error) -> {
			if (error != null || server == null) return;
			for (ServerPlayer player : PlayerLookup.all(server)) {
				ConfigSyncPacket.sendS2C(player);
			}
		});

		c2sRegistry.register(ConfigSyncPacket.PACKET_TYPE, ConfigSyncPacket.CODEC);
		registry.register(ConfigSyncPacket.PACKET_TYPE, ConfigSyncPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.PACKET_TYPE, ((packet, ctx) -> {
			if (ConfigSyncPacket.hasPermissionsToSendSync(ctx.player(), true))
				ConfigSyncPacket.receive(packet, ctx.server());
		}));

		registry.register(LocalPlayerSoundPacket.PACKET_TYPE, LocalPlayerSoundPacket.CODEC);
		registry.register(LocalSoundPacket.PACKET_TYPE, LocalSoundPacket.CODEC);
		registry.register(RelativeMovingSoundPacket.PACKET_TYPE, RelativeMovingSoundPacket.CODEC);
		registry.register(StartingMovingRestrictionSoundLoopPacket.PACKET_TYPE, StartingMovingRestrictionSoundLoopPacket.CODEC);
		registry.register(MovingRestrictionSoundPacket.PACKET_TYPE, MovingRestrictionSoundPacket.CODEC);
		registry.register(FlyBySoundPacket.PACKET_TYPE, FlyBySoundPacket.CODEC);
		registry.register(FadingDistanceSwitchingSoundPacket.PACKET_TYPE, FadingDistanceSwitchingSoundPacket.CODEC);
		registry.register(MovingFadingDistanceSwitchingRestrictionSoundPacket.PACKET_TYPE, MovingFadingDistanceSwitchingRestrictionSoundPacket.CODEC);
		registry.register(CooldownChangePacket.PACKET_TYPE, CooldownChangePacket.CODEC);
		registry.register(ForcedCooldownPacket.PACKET_TYPE, ForcedCooldownPacket.CODEC);
		registry.register(CooldownTickCountPacket.PACKET_TYPE, CooldownTickCountPacket.CODEC);
		registry.register(ScreenShakePacket.PACKET_TYPE, ScreenShakePacket.CODEC);
		registry.register(EntityScreenShakePacket.PACKET_TYPE, EntityScreenShakePacket.CODEC);
		registry.register(RemoveScreenShakePacket.PACKET_TYPE, RemoveScreenShakePacket.CODEC);
		registry.register(RemoveEntityScreenShakePacket.PACKET_TYPE, RemoveEntityScreenShakePacket.CODEC);
		registry.register(SpottingIconPacket.PACKET_TYPE, SpottingIconPacket.CODEC);
		registry.register(SpottingIconRemovePacket.PACKET_TYPE, SpottingIconRemovePacket.CODEC);
		registry.register(WindSyncPacket.PACKET_TYPE, WindSyncPacket.CODEC);
		registry.register(WindDisturbancePacket.PACKET_TYPE, WindDisturbancePacket.CODEC);
		registry.register(PlayerStructureStatusPacket.PACKET_TYPE, PlayerStructureStatusPacket.CODEC);

		// CAPE
		registry.register(CapeCustomizePacket.PACKET_TYPE, CapeCustomizePacket.CODEC);
		registry.register(LoadCapeRepoPacket.PACKET_TYPE, LoadCapeRepoPacket.STREAM_CODEC);
		c2sRegistry.register(CapeCustomizePacket.PACKET_TYPE, CapeCustomizePacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(CapeCustomizePacket.PACKET_TYPE,
			(packet, ctx) -> {
				UUID uuid = ctx.player().getUUID();
				ResourceLocation capeId = packet.getCapeId();
				if (capeId == null || CapeUtil.canPlayerUserCape(uuid, capeId)) {
					CapeCustomizePacket.sendCapeToAll(ctx.server(), uuid, capeId);
				}
			}
		);

		// FILE TRANSFER
		registry.register(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC);
		c2sRegistry.register(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE,
			(packet, ctx) -> {
				if (packet.request()) {
					String requestPath = packet.transferPath();
					String fileName = packet.fileName();
					if (!FileTransferFilter.isRequestAcceptable(requestPath, fileName, ctx.player())) return;

					File file = ctx.server().getServerDirectory().resolve(requestPath).resolve(fileName).toFile();
					try {
						for (FileTransferPacket fileTransferPacket : FileTransferPacket.create(requestPath, file)) {
							ServerPlayNetworking.send(ctx.player(), fileTransferPacket);
						}
					} catch (IOException ignored) {
						FrozenLibConstants.LOGGER.error("Unable to create and send transfer packets for file {} on server!", fileName);
					}
				} else {
					if (!FrozenLibConfig.FILE_TRANSFER_SERVER) return;

					String destPath = packet.transferPath().replace("/.local", "");
					String fileName = packet.fileName();
					if (!FileTransferFilter.isTransferAcceptable(destPath, fileName, ctx.player())) return;

					try {
						Path path = ctx.server().getServerDirectory().resolve(destPath).resolve(packet.fileName());
						FileTransferRebuilder.onReceiveFileTransferPacket(path, packet.snippet(), packet.totalPacketCount(), false);
					} catch (IOException ignored) {
						FrozenLibConstants.LOGGER.error("Unable to save transferred file {} on server!", packet.fileName());
					}
				}
			}
		);

		// DEBUG
		registry.register(ImprovedGoalDebugPayload.PACKET_TYPE, ImprovedGoalDebugPayload.STREAM_CODEC);
		registry.register(GoalDebugRemovePayload.PACKET_TYPE, GoalDebugRemovePayload.STREAM_CODEC);
		registry.register(ImprovedGameEventListenerDebugPayload.PACKET_TYPE, ImprovedGameEventListenerDebugPayload.STREAM_CODEC);
		registry.register(ImprovedGameEventDebugPayload.PACKET_TYPE, ImprovedGameEventDebugPayload.STREAM_CODEC);
		registry.register(WindAccessPacket.PACKET_TYPE, WindAccessPacket.STREAM_CODEC);

		c2sRegistry.register(StructureDebugRequestPayload.PACKET_TYPE, StructureDebugRequestPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(StructureDebugRequestPayload.PACKET_TYPE,
			(packet, ctx) -> StructureDebugRequestPayload.sendBack(ctx.player(), ctx.player().level(), packet.chunkPos())
		);
	}

	public static void sendPacketToAllPlayers(@NotNull ServerLevel world, CustomPacketPayload payload) {
		Packet<?> packet = new ClientboundCustomPayloadPacket(payload);
		for (ServerPlayer serverPlayer : world.players()) serverPlayer.connection.send(packet);
	}

	public static boolean isLocalPlayer(Player player) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return false;

		return Minecraft.getInstance().isLocalPlayer(player.getGameProfile().getId());
	}

	public static boolean connectedToIntegratedServer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return false;

		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.hasSingleplayerServer();
	}

	/**
	 * @return if the client is connected to any server
	 */
	public static boolean connectedToServer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return false;

		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return false;

		return listener.getConnection().isConnected();
	}

	/**
	 * @return if the current server is multiplayer (LAN/dedicated) or not (singleplayer)
	 */
	public static boolean isMultiplayer() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return true;
		return !Minecraft.getInstance().isSingleplayer();
	}

}
