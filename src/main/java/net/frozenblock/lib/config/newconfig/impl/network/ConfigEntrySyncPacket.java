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

package net.frozenblock.lib.config.newconfig.impl.network;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.config.api.instance.ConfigModification;
import net.frozenblock.lib.config.impl.network.ConfigSyncPacket;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.registry.ConfigV2Registry;
import net.frozenblock.lib.config.newconfig.registry.ID;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.4
 */
public record ConfigEntrySyncPacket<T>(ID entryId, String className, T entryData) implements CustomPacketPayload {
	public static final Type<ConfigEntrySyncPacket<?>> PACKET_TYPE = new Type<>(FrozenLibConstants.id("config_entry_sync_packet"));
	// TODO: fix the codec to use entry stream codec
	public static final StreamCodec<FriendlyByteBuf, ConfigEntrySyncPacket<?>> CODEC = StreamCodec.ofMember(ConfigEntrySyncPacket::write, ConfigEntrySyncPacket::create);

	public static <T> ConfigEntrySyncPacket<T> create(FriendlyByteBuf buf) {
		final ID entryId = ID.parse(buf.readUtf());

		try {
			String className = buf.readUtf();
			final T entryData = ConfigEntryByteBufUtil.readUbjson(buf, entryId, className);
			return new ConfigEntrySyncPacket<>(entryId, className, entryData);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read config data from packet.", e);
		}
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.entryId.toString());
		buf.writeUtf(this.className);
		try {
			ConfigEntryByteBufUtil.writeUbjson(buf, this.entryId, this.entryData);
		} catch (Exception e) {
			throw new RuntimeException("Failed to write config data to packet.", e);
		}
	}

	public static <T> void receive(ConfigEntrySyncPacket<T> packet, @Nullable MinecraftServer server) {
		final ID entryId = packet.entryId();

		final ConfigEntry<?> raw = ConfigV2Registry.CONFIG_ENTRY.get(entryId);

		final ConfigEntry<T> entry = (ConfigEntry<T>) raw;
		if (server != null) {
			// C2S logic
			ConfigModification.copyInto(packet.entryData(), entry.getActual());
			if (!FrozenNetworking.connectedToIntegratedServer()) entry.getConfigData().save();
			for (ServerPlayer player : PlayerLookup.all(server)) sendS2C(player, List.of(entry));
		} else {
			// S2C logic
			entry.setSyncedValue(packet.entryData());
		}
		//entry.onSync(packet.entryData());
    }

	public static void sendS2C(ServerPlayer player, Iterable<ConfigEntry<?>> entries) {
		if (FrozenNetworking.isLocalPlayer(player)) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry.getId(), entry.entryClass().getName(), entry.get());
			ServerPlayNetworking.send(player, packet);
		}
	}

	public static void sendS2C(ServerPlayer player) {
		sendS2C(player, ConfigV2Registry.CONFIG_ENTRY.values());
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S(Iterable<ConfigEntry<?>> entries) {
		if (!ClientPlayNetworking.canSend(PACKET_TYPE)) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry.getId(), entry.entryClass().getName(), entry.getActual());
			ClientPlayNetworking.send(packet);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S() {
		sendC2S(ConfigV2Registry.CONFIG_ENTRY.values());
	}

	@Environment(EnvType.CLIENT)
	public static <T> void trySendC2S(ConfigEntry<T> config) {
		if (ConfigSyncPacket.hasPermissionsToSendSync(Minecraft.getInstance().player, false))
			sendC2S(List.of(config));
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
