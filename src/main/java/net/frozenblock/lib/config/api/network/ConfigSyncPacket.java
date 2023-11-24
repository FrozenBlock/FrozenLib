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

package net.frozenblock.lib.config.api.network;

import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.4.5
 */
public record ConfigSyncPacket<T>(
	String modId,
	String className,
	T configData
) implements FabricPacket {
	public static final PacketType<ConfigSyncPacket<?>> PACKET_TYPE = PacketType.create(FrozenMain.CONFIG_SYNC_PACKET, ConfigSyncPacket::create);

	@Nullable
	public static <T> ConfigSyncPacket<T> create(FriendlyByteBuf buf) {
		String modId = buf.readUtf();
		String className = buf.readUtf();
		try {
			T configData = ConfigByteBufUtil.readJankson(buf, modId, className);
			return new ConfigSyncPacket<>(modId, className, configData);
		} catch (SyntaxError | ClassNotFoundException e) {
			FrozenLogUtils.error("Failed to read config data from packet.", true, e);
			return null;
		}
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(modId);
		buf.writeUtf(className);
		ConfigByteBufUtil.writeJankson(buf, modId, configData);
	}

	public static <T> void receive(ConfigSyncPacket<T> packet) {
		String modId = packet.modId();
		String className = packet.className();
        for (Config<?> raw : ConfigRegistry.getConfigsForMod(modId)) {
			String configClassName = raw.configClass().getName();
            if (!configClassName.equals(className)) continue;
			Config<T> config = (Config<T>) raw;
			boolean shouldAddModification = !ConfigRegistry.containsSyncData(config);
			ConfigRegistry.setSyncData(config, new ConfigSyncData<>(packet.configData()));
            if (shouldAddModification) {
				ConfigRegistry.register(
					config,
					new ConfigModification<>(
						new ConfigSyncModification<>(config, ConfigRegistry::getSyncData)
					),
					Integer.MIN_VALUE // make sure its set first
				);
			}
			break;
        }
    }

	public static void sendS2C(ServerPlayer player, Iterable<Config<?>> configs) {
		for (Config<?> config : configs) {
			if (!config.supportsModification()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.configWithoutSync());
			ServerPlayNetworking.send(player, packet);
		}
	}

	public static void sendS2C(ServerPlayer player) {
		sendS2C(player, ConfigRegistry.getAllConfigs());
	}

	public static void sendC2S(Iterable<Config<?>> configs) {
		for (Config<?> config : configs) {
			if (!config.supportsModification()) continue;
			ConfigSyncPacket<?> packet = new ConfigSyncPacket<>(config.modId(), config.configClass().getName(), config.configWithoutSync());
			ClientPlayNetworking.send(packet);
		}
	}

	public static void sendC2S() {
		sendC2S(ConfigRegistry.getAllConfigs());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
