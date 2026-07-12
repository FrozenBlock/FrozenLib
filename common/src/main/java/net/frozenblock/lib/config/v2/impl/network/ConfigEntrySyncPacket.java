/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.impl.network;

import java.util.Collection;
import java.util.List;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.FrozenLibLogUtils;
import net.frozenblock.lib.config.v2.config.ConfigData;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.lib.networking.api.PlayerLookup;
import net.frozenblock.lib.platform.ModLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.4
 */
@ApiStatus.Internal
public record ConfigEntrySyncPacket<T>(ConfigEntry entry, T value) implements CustomPacketPayload {
	private static final ConfigEntrySyncPacket DUMMY_PACKET = new ConfigEntrySyncPacket(null, null);
	public static final Type<ConfigEntrySyncPacket<?>> PACKET_TYPE = new Type<>(FrozenLibConstants.id("config_entry_sync_packet"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConfigEntrySyncPacket<?>> CODEC = StreamCodec.ofMember(ConfigEntrySyncPacket::write, ConfigEntrySyncPacket::create);

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
		if (ModLoader.isServer()) return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
		if (!NetworkingHelper.connectedToServer()) return false;

		final boolean isHost = serverSide && NetworkingHelper.isLocalPlayer(player);
		return NetworkingHelper.connectedToIntegratedServer() || isHost || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.entry.id().toString());
		this.entry.streamCodec().encode(buf, this.entry.getActual());
	}

	public static void receive(ConfigEntrySyncPacket packet, @Nullable ServerPlayer sender, @Nullable MinecraftServer server) {
		if (packet == DUMMY_PACKET) return;
		if ((sender == null) == (server != null)) {
			FrozenLibLogUtils.logError("Config sync received with invalid sender!", FrozenLibLogUtils.UNSTABLE_LOGGING);
			return;
		}

		final ConfigEntry entry = packet.entry();
		if (sender != null) {
			// C2S logic
			FrozenLibLogUtils.log("ENTRY SYNC RECEIVED ON SERVER: " + entry.id(), FrozenLibLogUtils.UNSTABLE_LOGGING);
			if (NetworkingHelper.isLocalPlayer(sender)) {
				for (ServerPlayer player : PlayerLookup.all(server)) {
					ConfigEntrySyncPacket.sendEntryS2C(player, List.of(entry));
				}
			} else {
				entry.setValue(packet.value());
			}

			if (!NetworkingHelper.connectedToIntegratedServer()) entry.configData().save();
		} else {
			// S2C logic
			FrozenLibLogUtils.log("ENTRY SYNC RECEIVED ON CLIENT: " + entry.id(), FrozenLibLogUtils.UNSTABLE_LOGGING);
			entry.setSyncedValue(packet.value());
		}
		//entry.onSync(packet.value());
    }

	public static void sendDataS2C(ServerPlayer player, Collection<ConfigData<?>> entries) {
		if (NetworkingHelper.isLocalPlayer(player)) return;
		for (ConfigData<?> entry : entries) sendEntryS2C(player, entry.entries().values());
	}

	public static void sendEntryS2C(ServerPlayer player, Iterable<ConfigEntry<?>> entries) {
		if (NetworkingHelper.isLocalPlayer(player)) return;

		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			final ConfigEntrySyncPacket<?> packet = new ConfigEntrySyncPacket<>(entry, entry.get());
			NetworkingHelper.sendToPlayer(player, packet);
		}

		if (!FrozenLibLogUtils.UNSTABLE_LOGGING) return;

		boolean hadEntryBefore = false;
		final StringBuilder builder = new StringBuilder("ENTRY SYNC SENT FROM SERVER:");
		for (ConfigEntry<?> entry : entries) {
			if (!entry.isSyncable()) continue;
			builder.append(hadEntryBefore ? ", " : " ").append(entry.id());
			hadEntryBefore = true;
		}

		FrozenLibLogUtils.log(builder.toString(), FrozenLibLogUtils.UNSTABLE_LOGGING);
	}

	public static void sendS2C(ServerPlayer player) {
		sendEntryS2C(player, ConfigV2Registry.allConfigEntries());
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
