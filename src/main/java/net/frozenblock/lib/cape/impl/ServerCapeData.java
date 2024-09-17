/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.cape.impl;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.cape.api.CapeUtil;
import net.frozenblock.lib.cape.impl.networking.CapeCustomizePacket;
import net.minecraft.server.level.ServerPlayer;

public class ServerCapeData {
	private static final Map<UUID, Cape> CAPES_IN_SERVER = new HashMap<>();

	public static void sendAllCapesToPlayer(ServerPlayer recipent) {
		CAPES_IN_SERVER.forEach((uuid, cape) -> ServerPlayNetworking.send(recipent, CapeCustomizePacket.createPacket(uuid, cape)));
	}

	public static void init() {
		ServerLifecycleEvents.SERVER_STOPPING.register(client -> CAPES_IN_SERVER.clear());
		ServerPlayConnectionEvents.DISCONNECT.register((serverGamePacketListener, minecraftServer) -> {
			UUID uuid = serverGamePacketListener.getPlayer().getUUID();
			if (CAPES_IN_SERVER.remove(uuid) != null) {
				for (ServerPlayer serverPlayer : PlayerLookup.all(minecraftServer)) {
					ServerPlayNetworking.send(serverPlayer, CapeCustomizePacket.createDisablePacket(uuid));
				}
			}
		});

		List<UUID> devs = ImmutableList.of(
			UUID.fromString("097b76e8-ac32-410f-b81c-38dd4086b97c"), // Lunade
			UUID.fromString("e4d5386a-2255-450b-9478-17ed2a31041d"), // Tree
			UUID.fromString("62af0c47-6817-45be-8147-6adc2c9681c3"), // Soro
			UUID.fromString("ccaa0664-8fd4-4176-96b4-eab6f8c75083"), // Alex
			UUID.fromString("659b74f7-b151-426a-b9c4-a71cd2fb64c6") // Liuk
		);
		List<UUID> artists = ImmutableList.of(
			UUID.fromString("321a6c75-182f-4de0-b660-6b48e6853f7a"), // Zhen
			UUID.fromString("7e6a4565-4f83-40a7-8fc1-ca14547b9fcd"), // Voxelotl
			UUID.fromString("a851a138-cf93-4b1e-b94b-9361a7343f00") // SargentReckless
		);
		List<UUID> builders = ImmutableList.of(
			UUID.fromString("b7d99111-f8b8-4627-a7a7-1ef3d58708f5"), // Wiggle
			UUID.fromString("2d12cab0-7338-44ed-96de-b6da49c7f07c") // Rebel
		);
		List<UUID> composers = ImmutableList.of(
			UUID.fromString("321a6c75-182f-4de0-b660-6b48e6853f7a"), // Zhen
			UUID.fromString("ce9dd341-b1c2-44d9-a014-71e11d163b01") // LudoCrypt
		);

		ArrayList<UUID> allArray = new ArrayList<>(devs);
		allArray.addAll(artists);
		allArray.addAll(builders);
		allArray.addAll(composers);

		CapeUtil.registerCapeWithWhitelist(FrozenSharedConstants.id("frozenblock"), allArray);
	}
}