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

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.newconfig.config.ConfigData;
import net.frozenblock.lib.config.newconfig.entry.ConfigEntry;
import net.frozenblock.lib.config.newconfig.modification.ConfigEntryModification;
import net.frozenblock.lib.config.newconfig.registry.ConfigV2Registry;
import net.frozenblock.lib.config.newconfig.registry.ID;
import net.frozenblock.lib.networking.FrozenClientNetworking;
import net.frozenblock.lib.networking.FrozenNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.4
 */
public record ConfigEntrySyncPacket<T>(ConfigEntry entry, T value) implements CustomPacketPayload {
	private static final ConfigEntrySyncPacket DUMMY_PACKET = new ConfigEntrySyncPacket(null, null);
	public static final Type<ConfigEntrySyncPacket<?>> PACKET_TYPE = new Type<>(FrozenLibConstants.id("config_entry_sync_packet"));
	public static final StreamCodec<FriendlyByteBuf, ConfigEntrySyncPacket<?>> CODEC = StreamCodec.ofMember(ConfigEntrySyncPacket::write, ConfigEntrySyncPacket::create);

	public static ConfigEntrySyncPacket create(FriendlyByteBuf buf) {
		final ID entryId = ID.parse(buf.readUtf());
		final ConfigEntry entry = ConfigV2Registry.getEntry(entryId);
		if (entry == null) {
			FrozenLibLogUtils.logError("Unable to find config entry with id: " + entryId);
			return DUMMY_PACKET;
		}

		try {
			final StreamCodec streamCodec = entry.streamCodec();
			final Object value = streamCodec.decode(buf);
			return new ConfigEntrySyncPacket<>(entry, value);
		} catch (Exception e) {
			FrozenLibLogUtils.logError("Failed to read config data from packet.", e);
			return DUMMY_PACKET;
		}
	}

	public static boolean hasPermissionsToSendSync(@Nullable Player player, boolean serverSide) {
		if (player == null) return false;
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
		if (FrozenClientNetworking.notConnected()) return false;

		final boolean isHost = serverSide && FrozenNetworking.isLocalPlayer(player);
		return FrozenNetworking.connectedToIntegratedServer() || isHost || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.entry.id().toString());
		this.entry.streamCodec().encode(buf, this.entry.getActual());
	}

	public static void receive(ConfigEntrySyncPacket packet, @Nullable MinecraftServer server) {
		if (packet == DUMMY_PACKET) return;

		final ConfigEntry entry = packet.entry();
		if (server != null) {
			// C2S logic

			// TODO: explain to LunadeMusic what this line does lol
			ConfigEntryModification.copyInto(packet.value(), entry.getActual());
			if (!FrozenNetworking.connectedToIntegratedServer()) entry.configData().save();
			for (ServerPlayer player : PlayerLookup.all(server)) sendEntryS2C(player, List.of(entry));
		} else {
			// S2C logic
			entry.setSyncedValue(packet.value());
		}
		//entry.onSync(packet.value());
    }

	public static void sendDataS2C(ServerPlayer player, Collection<ConfigData<?>> entries) {
		if (FrozenNetworking.isLocalPlayer(player)) return;

		for (ConfigData<?> entry : entries) {
			sendEntryS2C(player, entry.entries().values());
		}
	}

	public static void sendEntryS2C(ServerPlayer player, Iterable<ConfigEntry<?>> entries) {
		if (FrozenNetworking.isLocalPlayer(player)) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry, entry.get());
			ServerPlayNetworking.send(player, packet);
		}
	}

	public static void sendS2C(ServerPlayer player) {
		sendEntryS2C(player, ConfigV2Registry.allConfigEntries());
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S(Iterable<ConfigEntry<?>> entries) {
		if (!ClientPlayNetworking.canSend(PACKET_TYPE)) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry, entry.getActual());
			ClientPlayNetworking.send(packet);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void sendC2S() {
		sendC2S(ConfigV2Registry.allConfigEntries());
	}

	@Environment(EnvType.CLIENT)
	public static <T> void trySendC2S(ConfigEntry<T> config) {
		if (hasPermissionsToSendSync(Minecraft.getInstance().player, false)) sendC2S(List.of(config));
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
