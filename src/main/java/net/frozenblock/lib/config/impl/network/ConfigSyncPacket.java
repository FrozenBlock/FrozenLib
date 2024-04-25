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

package net.frozenblock.lib.config.impl.network;

import blue.endless.jankson.api.SyntaxError;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.network.ConfigByteBufUtil;
import net.frozenblock.lib.config.api.sync.network.ConfigSyncData;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.5
 */
public record ConfigSyncPacket<T>(
	String modId,
	String className,
	T configData
) implements FabricPacket {

	public static final int PERMISSION_LEVEL = 2;

	public static final PacketType<ConfigSyncPacket<?>> PACKET_TYPE = PacketType.create(
		FrozenSharedConstants.id("config_sync_packet"),
		ConfigSyncPacket::create
	);

	@Nullable
	public static <T> ConfigSyncPacket<T> create(@NotNull FriendlyByteBuf buf) {
		String modId = buf.readUtf();
		String className = buf.readUtf();
		try {
			T configData = ConfigByteBufUtil.readJankson(buf, modId, className);
			return new ConfigSyncPacket<>(modId, className, configData);
		} catch (SyntaxError | ClassNotFoundException e) {
			FrozenLogUtils.logError("Failed to read config data from packet.", true, e);
			return null;
		}
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeUtf(modId);
		buf.writeUtf(className);
		ConfigByteBufUtil.writeJankson(buf, modId, configData);
	}

	public static <T> void receive(@NotNull ConfigSyncPacket<T> packet, @Nullable MinecraftServer server) {
		String modId = packet.modId();
		String className = packet.className();
        for (Config<?> raw : ConfigRegistry.getConfigsForMod(modId)) {
			String configClassName = raw.configClass().getName();
            if (!configClassName.equals(className)) continue;
			Config<T> config = (Config<T>) raw;
			if (server != null) {
				// C2S logic
				ConfigModification.copyInto(packet.configData(), config.instance());
				if (!FrozenNetworking.connectedToIntegratedServer())
					config.save();
				for (ServerPlayer player : PlayerLookup.all(server)) {
					sendS2C(player, List.of(config));
				}
			} else {
				// S2C logic
				boolean shouldAddModification = !ConfigRegistry.containsSyncData(config);
				ConfigRegistry.setSyncData(config, new ConfigSyncData<>(packet.configData()));
				if (shouldAddModification) {
					ConfigRegistry.register(
						config,
						new ConfigModification<>(
							new ConfigSyncModification<>(config, ConfigRegistry::getSyncData)
						),
						Integer.MIN_VALUE // make sure it's the first modification
					);
				}
			}
			break;
        }
    }

	public static void sendS2C(ServerPlayer player, @NotNull Iterable<Config<?>> configs) {
		if (FrozenNetworking.isLocalPlayer(player))
			return;

		for (Config<?> config : configs) {
			if (!config.supportsSync()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.config());
			ServerPlayNetworking.send(player, packet);
		}
	}

	public static void sendS2C(ServerPlayer player) {
		sendS2C(player, ConfigRegistry.getAllConfigs());
	}

	public static boolean hasPermissionsToSendSync(@Nullable Player player, boolean serverSide) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return player.hasPermissions(PERMISSION_LEVEL);

		if (FrozenClientNetworking.notConnected())
			return false;

		boolean isHost = serverSide && FrozenNetworking.isLocalPlayer(player);
		return FrozenNetworking.connectedToIntegratedServer() || isHost || player.hasPermissions(PERMISSION_LEVEL);
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S(@NotNull Iterable<Config<?>> configs) {
		if (!ClientPlayNetworking.canSend(PACKET_TYPE)) return;

		for (Config<?> config : configs) {
			if (!config.supportsSync()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.instance());
			ClientPlayNetworking.send(packet);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S() {
		sendC2S(ConfigRegistry.getAllConfigs());
	}

	@Environment(EnvType.CLIENT)
	public static <T> void trySendC2S(Config<T> config) {
		if (hasPermissionsToSendSync(Minecraft.getInstance().player, false))
			sendC2S(List.of(config));
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
