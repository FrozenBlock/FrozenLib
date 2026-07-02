/*
 * Copyright (C) 2026 FrozenBlock
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.LoadCapeRepoPacket;
import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.impl.network.ConfigEntrySyncPacket;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.event.api.events.ClientConnectionEvents;
import net.frozenblock.lib.platform.FrozenLibInitPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public final class FrozenClientNetworking {

	public static void registerClientReceivers() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(ConfigEntrySyncPacket.PACKET_TYPE, (packet, minecraft, player) ->
			ConfigEntrySyncPacket.receive(packet, null, null)
		);
		ClientConnectionEvents.DISCONNECT.register((handler, client) -> {
			for (ConfigEntry<?> config : ConfigV2Registry.allConfigEntries()) ConfigSyncModification.clearSyncData(config);
		});

		receiveCapeRepoPacket();
	}

	private static void receiveCapeRepoPacket() {
		FrozenLibInitPlatformUtils.NETWORKING.registerGlobalClientReceiver(LoadCapeRepoPacket.PACKET_TYPE, (packet, minecraft, player) -> {
			CapeUtil.registerCapesFromURL(packet.capeRepo());
		});
	}

	public static boolean notConnected() {
		final Minecraft minecraft = Minecraft.getInstance();
		final ClientPacketListener listener = minecraft.getConnection();
		if (listener == null) return true;

		final LocalPlayer player = Minecraft.getInstance().player;
		return player == null;
	}

	public static boolean connectedToLan() {
		if (notConnected()) return false;
		final ServerData serverData = Minecraft.getInstance().getCurrentServer();
		if (serverData == null) return false;
		return serverData.isLan();
	}
}
